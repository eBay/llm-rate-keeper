package com.ebay.llm.ratekeeper.store.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class TokenStoreExceptionTest {

  @Test
  public void testExceptionMessage() {
    String message = "An error occurred";
    TokenStoreException exception = new TokenStoreException(message);

    assertEquals(message, exception.getMessage());
  }

  @Test
  public void testExceptionMessageAndCause() {
    String message = "An error occurred";
    Throwable cause = new RuntimeException("Cause of the error");
    TokenStoreException exception = new TokenStoreException(message, cause);

    assertEquals(message, exception.getMessage());
    assertSame(cause, exception.getCause());
  }
}