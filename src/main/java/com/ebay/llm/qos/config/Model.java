package com.ebay.llm.qos.config;

import lombok.Data;

@Data
public class Model {

  private String id;
  private String description;
  private Long apiLimitPerMinute;
  private Long tokensLimitPerRequest;
  private Long tokensLimitPerMinute;
  private Long coolingPeriodSeconds;
}