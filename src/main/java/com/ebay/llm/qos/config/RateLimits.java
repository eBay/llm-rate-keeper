package com.ebay.llm.qos.config;

import lombok.Data;


@Data
public class RateLimits {

  private Integer apiLimitPerMinute;
  private Integer tokensLimitPerMinute;
  private Integer tokensLimitPerDay;
}