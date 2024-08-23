package com.ebay.llm.ratekeeper.store;

import com.ebay.llm.ratekeeper.model.ConsumedTokens;
import com.ebay.llm.ratekeeper.store.exception.TokenStoreException;

public interface TokenStore {

  boolean hasTokens(String clientId, String modelId, long tokensPerMinuteLimit,
      long tokensPerDayLimit) throws TokenStoreException;

  ConsumedTokens consumeTokens(String clientId, String modelId, long tokens,
      long tokensPerMinuteLimit,
      long tokensPerDayLimit) throws TokenStoreException;

  void setCoolingPeriod(String modelId, int durationInMilliSeconds) throws TokenStoreException;

  boolean isModelReadyToServe(String modelId) throws TokenStoreException;

  void resetCoolingPeriod(String modelId) throws TokenStoreException;
}