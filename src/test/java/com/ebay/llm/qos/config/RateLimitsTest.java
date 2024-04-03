package com.ebay.llm.qos.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RateLimitsTest {

  @Test
  public void shouldMaintainCorrectApiLimitPerMinute() {
    RateLimits rateLimits = new RateLimits();
    rateLimits.setApiLimitPerMinute(100L);
    Assertions.assertEquals(100L, rateLimits.getApiLimitPerMinute());
  }

  @Test
  public void shouldMaintainCorrectTokensLimitPerMinute() {
    RateLimits rateLimits = new RateLimits();
    rateLimits.setTokensLimitPerMinute(200L);
    Assertions.assertEquals(200L, rateLimits.getTokensLimitPerMinute());
  }

  @Test
  public void shouldMaintainCorrectTokensLimitPerDay() {
    RateLimits rateLimits = new RateLimits();
    rateLimits.setTokensLimitPerDay(5000L);
    Assertions.assertEquals(5000L, rateLimits.getTokensLimitPerDay());
  }

  @Test
  public void shouldHandleNullApiLimitPerMinute() {
    RateLimits rateLimits = new RateLimits();
    rateLimits.setApiLimitPerMinute(null);
    Assertions.assertNull(rateLimits.getApiLimitPerMinute());
  }

  @Test
  public void shouldHandleNullTokensLimitPerMinute() {
    RateLimits rateLimits = new RateLimits();
    rateLimits.setTokensLimitPerMinute(null);
    Assertions.assertNull(rateLimits.getTokensLimitPerMinute());
  }

  @Test
  public void shouldHandleNullTokensLimitPerDay() {
    RateLimits rateLimits = new RateLimits();
    rateLimits.setTokensLimitPerDay(null);
    Assertions.assertNull(rateLimits.getTokensLimitPerDay());
  }

  @Test
  public void shouldHandleMaximumApiLimitPerMinute() {
    RateLimits rateLimits = new RateLimits();
    rateLimits.setApiLimitPerMinute(Long.MAX_VALUE);
    Assertions.assertEquals(Long.MAX_VALUE, rateLimits.getApiLimitPerMinute());
  }

  @Test
  public void shouldHandleMaximumTokensLimitPerMinute() {
    RateLimits rateLimits = new RateLimits();
    rateLimits.setTokensLimitPerMinute(Long.MAX_VALUE);
    Assertions.assertEquals(Long.MAX_VALUE, rateLimits.getTokensLimitPerMinute());
  }

  @Test
  public void shouldHandleMaximumTokensLimitPerDay() {
    RateLimits rateLimits = new RateLimits();
    rateLimits.setTokensLimitPerDay(Long.MAX_VALUE);
    Assertions.assertEquals(Long.MAX_VALUE, rateLimits.getTokensLimitPerDay());
  }
}