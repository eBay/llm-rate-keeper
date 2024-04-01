package com.ebay.llm.qos.config;

import lombok.Data;

@Data
public class GlobalSettings {

  private Integer apiCallsLimitPerMinute;
  private Integer tokensLimitPerMinute;
  private Integer tokensLimitPerDay;
  private Integer coolingPeriodSeconds;
}