package com.ebay.llm.qos.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PairTest {

  private Pair<String, Integer> pair;

  @BeforeEach
  public void setup() {
    pair = new Pair<>("Test", 123);
  }

  @Test
  public void getFirstReturnsCorrectValue() {
    Assertions.assertEquals("Test", pair.getFirst());
  }

  @Test
  public void getSecondReturnsCorrectValue() {
    Assertions.assertEquals(123, pair.getSecond());
  }

  @Test
  public void getFirstReturnsNullForNullFirstValue() {
    Pair<String, Integer> nullFirstPair = new Pair<>(null, 123);
    Assertions.assertNull(nullFirstPair.getFirst());
  }

  @Test
  public void getSecondReturnsNullForNullSecondValue() {
    Pair<String, Integer> nullSecondPair = new Pair<>("Test", null);
    Assertions.assertNull(nullSecondPair.getSecond());
  }
}