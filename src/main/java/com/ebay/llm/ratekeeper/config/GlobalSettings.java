package com.ebay.llm.ratekeeper.config;

import lombok.Data;

@Data
public class GlobalSettings {

  private Long apiCallsLimitPerMinute;
  private Long tokensLimitPerMinute;
  private Long tokensLimitPerDay;
  private Long coolingPeriodSeconds;
  private String nuKVHost;

}