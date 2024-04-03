package com.ebay.llm.qos.service;

import com.ebay.llm.qos.config.Client;
import com.ebay.llm.qos.config.ClientModel;
import com.ebay.llm.qos.config.ConfigLoader;
import com.ebay.llm.qos.config.ModelClientConfig;
import com.ebay.llm.qos.constant.TokenStoreEnum;
import com.ebay.llm.qos.store.TokenStore;
import com.ebay.llm.qos.store.exception.TokenStoreException;
import io.lettuce.core.RedisClient;
import java.io.IOException;

public class RateLimiter {

  private TokenStore tokenStore;
  private ModelClientConfig config;

  public RateLimiter(TokenStoreEnum tokenStoreEnum, RedisClient redisClient, boolean isAsync)
      throws IOException {
    if (redisClient == null) {
      throw new TokenStoreException("Failed to establish a connection with Redis");
    }
    this.tokenStore = TokenStoreFactory.createTokenStore(tokenStoreEnum, redisClient, isAsync);
    this.config = new ConfigLoader().loadConfig("model-client-config.yml");
  }

  public boolean isAllowed(String modelId, String clientId, int tokensRequired) {
    return tokenStore.hasTokens(clientId, modelId, tokensRequired, tokensRequired);
  }

  public void updateTokenUsage(String modelId, String clientId, long contextTokensUsed,
      long completeTokensUsed) {
    long usedTokenCount = contextTokensUsed + completeTokensUsed;
    Pair<Long, Long> tokenLimits = getTokenLimits(clientId, modelId);

    tokenStore.consumeTokens(clientId, modelId, usedTokenCount, tokenLimits.getFirst(),
        tokenLimits.getSecond());
  }

  public boolean isModelCanServe(String modelId) {
    return tokenStore.isModelReadyToServe(modelId);
  }

  public void registerCoolingPeriod(String modelId, long coolDownPeriodInSeconds) {
    tokenStore.setCoolingPeriod(modelId, (int) coolDownPeriodInSeconds * 1000);
  }

  public Pair<Long, Long> getTokenLimits(String clientId, String modelId) {
    Client client = config.getClientConfig(clientId);
    ClientModel model = client.getModelConfig(modelId);
    return new Pair<>(model.getTokensLimitPerMinute(), model.getTokensLimitPerDay());
  }
}