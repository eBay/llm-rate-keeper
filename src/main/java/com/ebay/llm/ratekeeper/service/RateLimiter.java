package com.ebay.llm.ratekeeper.service;

import com.ebay.llm.ratekeeper.config.Client;
import com.ebay.llm.ratekeeper.config.ClientModel;
import com.ebay.llm.ratekeeper.config.ConfigLoader;
import com.ebay.llm.ratekeeper.config.ModelClientConfig;
import com.ebay.llm.ratekeeper.model.ConsumedTokens;
import com.ebay.llm.ratekeeper.store.TokenStore;
import java.io.IOException;

public class RateLimiter {

  private final TokenStore tokenStore;
  private final ModelClientConfig config;

  public RateLimiter(TokenStore tokenStore)
      throws IOException {
    this.tokenStore = tokenStore;
    this.config = new ConfigLoader().loadConfig("model-client-config.yml");
  }

  public boolean isAllowed(String modelId, String clientId, int tokensRequired) {
    Pair<Long, Long> tokenLimits = getTokenLimits(clientId, modelId);
    updateTokenUsage(modelId, clientId, 0, 0);
    return tokenStore.hasTokens(clientId, modelId, tokenLimits.getFirst(), tokenLimits.getSecond());
  }

  public ConsumedTokens updateTokenUsage(String modelId, String clientId, long contextTokensUsed,
      long completeTokensUsed) {
    long usedTokenCount = contextTokensUsed + completeTokensUsed;
    Pair<Long, Long> tokenLimits = getTokenLimits(clientId, modelId);

    return tokenStore.consumeTokens(clientId, modelId, usedTokenCount, tokenLimits.getFirst(),
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