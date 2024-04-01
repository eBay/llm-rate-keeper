package com.ebay.llm.qos.config;

import lombok.Data;

@Data
public class DefaultClientTokenLimits {

  private Long tokensLimitPerMinute;
  private Long tokensLimitPerDay;
}