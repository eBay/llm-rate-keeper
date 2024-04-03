package com.ebay.llm.qos.store.nukv;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NUKVTokenStoreTest {

  private NUKVTokenStore nukvTokenStore;

  @BeforeEach
  public void setup() {
    nukvTokenStore = new NUKVTokenStore();
  }

  @Test
  public void hasTokensThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> {
      nukvTokenStore.hasTokens("clientId", "modelId", 100L, 1000L);
    });
  }

  @Test
  public void consumeTokensThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> {
      nukvTokenStore.consumeTokens("clientId", "modelId", 100L, 1000L, 10000L);
    });
  }

  @Test
  public void setCoolingPeriodThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> {
      nukvTokenStore.setCoolingPeriod("modelId", 1000);
    });
  }

  @Test
  public void isModelReadyToServeThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> {
      nukvTokenStore.isModelReadyToServe("modelId");
    });
  }

  @Test
  public void resetCoolingPeriodThrowsUnsupportedOperationException() {
    Assertions.assertThrows(UnsupportedOperationException.class, () -> {
      nukvTokenStore.resetCoolingPeriod("modelId");
    });
  }
}