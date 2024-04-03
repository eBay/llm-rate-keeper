package com.ebay.llm.qos.config;

import lombok.Data;


@Data
public class RateLimits {

  private Long apiLimitPerMinute;
  private Long tokensLimitPerMinute;
  private Long tokensLimitPerDay;
}