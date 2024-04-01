package com.ebay.llm.qos.store.redis.it;


import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ebay.llm.qos.store.redis.RedisTokenStore;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RedisTokenStoreIT {


  private RedisTokenStore redisTokenStore;
  private RedisClient redisClient;
  private StatefulRedisConnection<String, String> connection;

  @BeforeEach
  public void setup() {
    redisClient = RedisClient.create(
        "redis://localhost:6379"); // Adjust this to your test Redis server
    redisTokenStore = new RedisTokenStore(redisClient, false);
    connection = redisClient.connect();
  }

  @Test
  public void testHasTokens() {
    // Setup
    String clientId = "testClient";
    String modelId = "testModel";
    long tokensPerMinuteLimit = 100;
    long tokensPerDayLimit = 1000;

    // Execute
    boolean result = redisTokenStore.hasTokens(clientId, modelId, tokensPerMinuteLimit,
        tokensPerDayLimit);

    // Validate
    assertTrue(result, "Expected true when checking if client has tokens");
  }

  @Test
  public void testConsumeTokens() {
    // Setup
    String clientId = "testClient";
    String modelId = "testModel";
    long tokens = 10;
    long tokensPerMinuteLimit = 100;
    long tokensPerDayLimit = 1000;

    // Execute
    redisTokenStore.consumeTokens(clientId, modelId, tokens, tokensPerMinuteLimit,
        tokensPerDayLimit);

    // Validate
    assertTrue(
        redisTokenStore.hasTokens(clientId, modelId, tokensPerMinuteLimit, tokensPerDayLimit),
        "Expected true when checking if client has tokens after consuming tokens");
  }


}
