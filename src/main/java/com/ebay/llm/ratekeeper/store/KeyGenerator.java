package com.ebay.llm.ratekeeper.store;

public class KeyGenerator {

  public String generateKey(String clientId, String modelId, String period) {
    return String.format("%s:%s:tokens:%s", clientId, modelId, period);
  }
}