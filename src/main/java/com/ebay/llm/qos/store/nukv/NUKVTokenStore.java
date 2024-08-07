package com.ebay.llm.qos.store.nukv;

import com.ebay.llm.qos.model.ConsumedTokens;
import com.ebay.llm.qos.store.TokenStore;
import com.ebay.llm.qos.store.exception.TokenStoreException;

public class NUKVTokenStore implements TokenStore {

  @Override
  public boolean hasTokens(String clientId, String modelId, long tokensPerMinuteLimit,
      long tokensPerDayLimit) throws TokenStoreException {
    throw new UnsupportedOperationException("Operation not supported");
  }

  @Override
  public ConsumedTokens consumeTokens(String clientId, String modelId, long tokens, long tokensPerMinuteLimit,
      long tokensPerDayLimit) throws TokenStoreException {
    throw new UnsupportedOperationException("Operation not supported");
  }

  @Override
  public void setCoolingPeriod(String modelId, int durationInMilliSeconds)
      throws TokenStoreException {
    throw new UnsupportedOperationException("Operation not supported");
  }

  @Override
  public boolean isModelReadyToServe(String modelId) throws TokenStoreException {
    throw new UnsupportedOperationException("Operation not supported");
  }

  @Override
  public void resetCoolingPeriod(String modelId) throws TokenStoreException {
    throw new UnsupportedOperationException("Operation not supported");
  }
}