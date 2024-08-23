package com.ebay.llm.ratekeeper.store;

import com.ebay.llm.ratekeeper.model.TokenCount;
import java.time.Instant;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class TokenCountManager {

  public Deque<TokenCount> getTokenCounts(String value) {
    Deque<TokenCount> tokenCounts = new LinkedList<>();
    if (value != null) {
      String[] entries = value.split(";");
      for (String entry : entries) {
        String[] parts = entry.split(",");
        long tokenCount = Long.parseLong(parts[0]);
        Instant timestamp = Instant.ofEpochMilli(Long.parseLong(parts[1]));
        tokenCounts.addLast(new TokenCount(tokenCount, timestamp));
      }
    }
    return tokenCounts;
  }

  public String updateTokenCounts(Deque<TokenCount> tokenCounts) {
    return tokenCounts.stream()
        .map(tc -> tc.getTokens() + "," + tc.getTimestamp().toEpochMilli())
        .collect(Collectors.joining(";"));
  }

  public void removeOldEntries(Deque<TokenCount> tokenCounts, int seconds) {
    Instant cutoff = Instant.now().minusSeconds(seconds);
    while (!tokenCounts.isEmpty() && tokenCounts.getFirst().getTimestamp().isBefore(cutoff)) {
      tokenCounts.removeFirst();
    }
  }

  public long sumTokens(Deque<TokenCount> tokenCounts) {
    return tokenCounts.stream().mapToLong(TokenCount::getTokens).sum();
  }
}