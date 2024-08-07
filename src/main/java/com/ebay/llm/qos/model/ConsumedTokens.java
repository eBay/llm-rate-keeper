package com.ebay.llm.qos.model;

import lombok.Data;

@Data
public class ConsumedTokens {
  private long perDayTokens;
  private long perMinTokens;

}
