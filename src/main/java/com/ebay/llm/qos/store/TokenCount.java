package com.ebay.llm.qos.store;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TokenCount {

  private long tokens;
  private Instant timestamp;

}
