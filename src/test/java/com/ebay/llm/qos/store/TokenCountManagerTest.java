package com.ebay.llm.qos.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ebay.llm.qos.model.TokenCount;
import java.time.Instant;
import java.util.Deque;
import java.util.LinkedList;
import org.junit.jupiter.api.Test;

public class TokenCountManagerTest {

  @Test
  void getTokenCounts_withValidString_returnsCorrectDeque() {
    TokenCountManager manager = new TokenCountManager();
    String value = "10,1633036800000;20,1633123200000";
    Deque<TokenCount> result = manager.getTokenCounts(value);

    assertEquals(2, result.size());
    assertEquals(10, result.getFirst().getTokens());
    assertEquals(Instant.ofEpochMilli(1633036800000L), result.getFirst().getTimestamp());
    assertEquals(20, result.getLast().getTokens());
    assertEquals(Instant.ofEpochMilli(1633123200000L), result.getLast().getTimestamp());
  }

  @Test
  void getTokenCounts_withNullString_returnsEmptyDeque() {
    TokenCountManager manager = new TokenCountManager();
    Deque<TokenCount> result = manager.getTokenCounts(null);

    assertTrue(result.isEmpty());
  }

  @Test
  void updateTokenCounts_withValidDeque_returnsCorrectString() {
    TokenCountManager manager = new TokenCountManager();
    Deque<TokenCount> tokenCounts = new LinkedList<>();
    tokenCounts.add(new TokenCount(10, Instant.ofEpochMilli(1633036800000L)));
    tokenCounts.add(new TokenCount(20, Instant.ofEpochMilli(1633123200000L)));

    String result = manager.updateTokenCounts(tokenCounts);

    assertEquals("10,1633036800000;20,1633123200000", result);
  }

  @Test
  void removeOldEntries_withOldEntries_removesCorrectEntries() {
    TokenCountManager manager = new TokenCountManager();
    Deque<TokenCount> tokenCounts = new LinkedList<>();
    tokenCounts.add(new TokenCount(10, Instant.now().minusSeconds(1000)));
    tokenCounts.add(new TokenCount(20, Instant.now().minusSeconds(500)));
    tokenCounts.add(new TokenCount(30, Instant.now()));

    manager.removeOldEntries(tokenCounts, 600);

    assertEquals(2, tokenCounts.size());
    assertEquals(20, tokenCounts.getFirst().getTokens());
    assertEquals(30, tokenCounts.getLast().getTokens());
  }

  @Test
  void sumTokens_withMultipleEntries_returnsCorrectSum() {
    TokenCountManager manager = new TokenCountManager();
    Deque<TokenCount> tokenCounts = new LinkedList<>();
    tokenCounts.add(new TokenCount(10, Instant.now()));
    tokenCounts.add(new TokenCount(20, Instant.now()));
    tokenCounts.add(new TokenCount(30, Instant.now()));

    long result = manager.sumTokens(tokenCounts);

    assertEquals(60, result);
  }
}