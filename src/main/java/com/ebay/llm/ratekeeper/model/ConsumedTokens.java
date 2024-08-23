package com.ebay.llm.ratekeeper.model;

import lombok.Data;

@Data
public class ConsumedTokens {

  private long perDayTokens;
  private long perMinTokens;

}
