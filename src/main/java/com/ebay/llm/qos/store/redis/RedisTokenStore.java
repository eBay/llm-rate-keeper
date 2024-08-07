package com.ebay.llm.qos.store.redis;

import com.ebay.llm.qos.model.ConsumedTokens;
import com.ebay.llm.qos.store.KeyGenerator;
import com.ebay.llm.qos.store.TokenCount;
import com.ebay.llm.qos.store.TokenCountManager;
import com.ebay.llm.qos.store.TokenStore;
import com.ebay.llm.qos.store.exception.TokenStoreException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisTokenStore implements TokenStore {

  private static final String COOLING_KEY_PREFIX = "cooling:";
  private static final String TRUE_VALUE = "true";

  private final RedisClient redisClient;
  private final StatefulRedisConnection<String, String> connection;
  private final TokenCountManager tokenCountManager;
  private final KeyGenerator keyGenerator;
  private final boolean isAsync;
  private RedisCommands<String, String> syncCommands;
  private RedisAsyncCommands<String, String> asyncCommands;

  public RedisTokenStore(RedisClient redisClient, boolean isAsync) {
    this.redisClient = redisClient;
    this.connection = redisClient.connect();
    this.isAsync = isAsync;
    if (isAsync) {
      this.asyncCommands = connection.async();
    } else {
      this.syncCommands = connection.sync();
    }
    this.keyGenerator = new KeyGenerator();
    this.tokenCountManager = new TokenCountManager();
    log.info("RedisTokenStore initialized with RedisClient");
  }

  @Override
  public boolean hasTokens(String clientId, String modelId, long tokensPerMinuteLimit,
      long tokensPerDayLimit) {
    try {
      boolean hasTokens = checkTokens(clientId, modelId, tokensPerMinuteLimit, tokensPerDayLimit);
      return hasTokens;
    } catch (Exception e) {
      throw new TokenStoreException(
          "Error checking tokens for client " + clientId + " and model " + modelId, e);
    }
  }

  @Override
  public ConsumedTokens consumeTokens(String clientId, String modelId, long tokens, long tokensPerMinuteLimit,
      long tokensPerDayLimit) {
    try {
      return consume(clientId, modelId, tokens, tokensPerMinuteLimit, tokensPerDayLimit);
    } catch (Exception e) {
      throw new TokenStoreException(
          "Error consuming tokens for client " + clientId + " and model " + modelId, e);
    }
  }

  @Override
  public void setCoolingPeriod(String modelId, int durationInMilliSeconds) {
    log.info("Setting cooling period of {} milliseconds for model {}", durationInMilliSeconds,
        modelId);
    if (isAsync) {
      asyncCommands.setex(COOLING_KEY_PREFIX + modelId, durationInMilliSeconds / 1000, TRUE_VALUE);
    } else {
      syncCommands.setex(COOLING_KEY_PREFIX + modelId, durationInMilliSeconds / 1000, TRUE_VALUE);
    }
    log.info("Set cooling period of {} milliseconds for model {}", durationInMilliSeconds, modelId);
  }

  @Override
  public boolean isModelReadyToServe(String modelId) {
    log.info("Checking if model {} is ready to serve", modelId);
    boolean isReady;
    try {
      if (isAsync) {
        isReady = asyncCommands.get(COOLING_KEY_PREFIX + modelId).get() == null;
      } else {
        isReady = syncCommands.get(COOLING_KEY_PREFIX + modelId) == null;
      }
    } catch (Exception e) {
      throw new TokenStoreException(
          "Error checking if model is ready to serve for model " + modelId, e);
    }
    log.info("Model {} is ready to serve: {}", modelId, isReady);
    return isReady;
  }

  @Override
  public void resetCoolingPeriod(String modelId) {
    log.info("Resetting cooling period for model {}", modelId);
    if (isAsync) {
      asyncCommands.del(COOLING_KEY_PREFIX + modelId);
    } else {
      syncCommands.del(COOLING_KEY_PREFIX + modelId);
    }
    log.info("Reset cooling period for model {}", modelId);
  }

  private boolean checkTokens(String clientId, String modelId, long tokensPerMinuteLimit,
      long tokensPerDayLimit) throws ExecutionException, InterruptedException {
    String minuteKey = keyGenerator.generateKey(clientId, modelId, "minute");
    String dayKey = keyGenerator.generateKey(clientId, modelId, "day");

    Deque<TokenCount> minuteCounts = getTokenCounts(minuteKey);
    Deque<TokenCount> dayCounts = getTokenCounts(dayKey);

    return tokenCountManager.sumTokens(minuteCounts) <= tokensPerMinuteLimit
        && tokenCountManager.sumTokens(dayCounts) <= tokensPerDayLimit;
  }

  private ConsumedTokens consume(String clientId, String modelId, long tokens, long tokensPerMinuteLimit,
      long tokensPerDayLimit) throws ExecutionException, InterruptedException {
    String minuteKey = keyGenerator.generateKey(clientId, modelId, "minute");
    String dayKey = keyGenerator.generateKey(clientId, modelId, "day");

    Deque<TokenCount> minuteCounts = getTokenCounts(minuteKey);
    Deque<TokenCount> dayCounts = getTokenCounts(dayKey);

    TokenCount tokenCount = new TokenCount(tokens, Instant.now());
    minuteCounts.addLast(tokenCount);
    dayCounts.addLast(tokenCount);

    tokenCountManager.removeOldEntries(minuteCounts, 60);
    tokenCountManager.removeOldEntries(dayCounts, 86400);

    updateTokenCounts(minuteKey, minuteCounts);
    updateTokenCounts(dayKey, dayCounts);
    ConsumedTokens consumedTokens = new ConsumedTokens();
    consumedTokens.setPerDayTokens(tokenCountManager.sumTokens(getTokenCounts(minuteKey)));
    consumedTokens.setPerDayTokens(tokenCountManager.sumTokens(getTokenCounts(dayKey)));
   return consumedTokens;
  }

  private Deque<TokenCount> getTokenCounts(String key) {
    try {
      String value;
      if (isAsync) {
        if (asyncCommands == null) {
          throw new IllegalStateException("asyncCommands has not been initialized");
        }
        value = asyncCommands.get(key).get();
      } else {
        if (syncCommands == null) {
          throw new IllegalStateException("syncCommands has not been initialized");
        }
        value = syncCommands.get(key);
      }
      return tokenCountManager.getTokenCounts(value);
    } catch (Exception e) {
      throw new TokenStoreException("Error getting token counts for key " + key, e);
    }
  }

  private void updateTokenCounts(String key, Deque<TokenCount> tokenCounts) {
    try {
      String value = tokenCountManager.updateTokenCounts(tokenCounts);
      if (isAsync) {
        asyncCommands.set(key, value);
      } else {
        syncCommands.set(key, value);
      }
    } catch (Exception e) {
      throw new TokenStoreException("Error updating token counts for key " + key, e);
    }
  }

  public void shutdown() {
    log.info("Shutting down RedisTokenStore");
    if (connection != null) {
      connection.close();
    }
    if (redisClient != null) {
      redisClient.shutdown();
    }
  }
}