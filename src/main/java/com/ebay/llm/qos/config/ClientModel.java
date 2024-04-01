package com.ebay.llm.qos.config;

import lombok.Data;

@Data
public class ClientModel {

  private String id;
  private Long tokensLimitPerMinute;
  private Long tokensLimitPerDay;
  private String fallback;

  public Long getTokensLimitPerDay() {
    return tokensLimitPerDay;
  }


}