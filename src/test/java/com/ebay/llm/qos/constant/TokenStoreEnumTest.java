package com.ebay.llm.qos.constant;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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