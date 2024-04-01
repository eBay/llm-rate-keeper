package com.ebay.llm.qos.config;

import lombok.Data;

@Data
public class Model {

  private String id;
  private String description;
  private Integer apiLimitPerMinute;
  private Integer tokensLimitPerRequest;
  private Integer tokensLimitPerMinute;
  private Integer coolingPeriodSeconds;
}