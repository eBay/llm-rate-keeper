package com.ebay.llm.ratekeeper.config;

import lombok.Data;


@Data
public class RateLimits {

  private Long apiLimitPerMinute;
  private Long tokensLimitPerMinute;
  private Long tokensLimitPerDay;
}