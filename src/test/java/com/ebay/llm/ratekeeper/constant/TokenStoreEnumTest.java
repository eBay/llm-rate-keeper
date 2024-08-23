package com.ebay.llm.ratekeeper.constant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TokenStoreEnumTest {

  @Test
  void enumContainsValidValues() {
    for (TokenStoreEnum value : TokenStoreEnum.values()) {
      assertNotNull(TokenStoreEnum.valueOf(value.name()));
    }
  }

  @Test
  void enumDoesNotContainInvalidValue() {
    assertThrows(IllegalArgumentException.class, () -> TokenStoreEnum.valueOf("INVALID"));
  }
}