package com.ebay.llm.qos.config;

import lombok.Data;

@Data
public class ClientModel {

  private String id;
  private Integer tokensLimitPerMinute;
  private Integer tokensLimitPerDay;
  private String fallback;
}