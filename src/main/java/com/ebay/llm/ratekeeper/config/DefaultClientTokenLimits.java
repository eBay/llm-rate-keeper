package com.ebay.llm.ratekeeper.config;

import lombok.Data;

@Data
public class DefaultClientTokenLimits {

  private Long tokensLimitPerMinute;
  private Long tokensLimitPerDay;
}