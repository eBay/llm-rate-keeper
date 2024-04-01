package com.ebay.llm.qos.config;

import lombok.Data;

@Data
public class DefaultClientTokenLimits {

  private Integer tokensLimitPerMinute;
  private Integer tokensLimitPerDay;
}