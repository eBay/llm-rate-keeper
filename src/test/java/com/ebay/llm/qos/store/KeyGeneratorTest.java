package com.ebay.llm.qos.store;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyGeneratorTest {

  @Test
  void generateKey_withValidInputs_returnsCorrectKey() {
    KeyGenerator keyGenerator = new KeyGenerator();
    String result = keyGenerator.generateKey("client1", "modelA", "daily");
    assertEquals("client1:modelA:tokens:daily", result);
  }

  @Test
  void generateKey_withEmptyClientId_returnsCorrectKey() {
    KeyGenerator keyGenerator = new KeyGenerator();
    String result = keyGenerator.generateKey("", "modelA", "daily");
    assertEquals(":modelA:tokens:daily", result);
  }

  @Test
  void generateKey_withEmptyModelId_returnsCorrectKey() {
    KeyGenerator keyGenerator = new KeyGenerator();
    String result = keyGenerator.generateKey("client1", "", "daily");
    assertEquals("client1::tokens:daily", result);
  }

  @Test
  void generateKey_withEmptyPeriod_returnsCorrectKey() {
    KeyGenerator keyGenerator = new KeyGenerator();
    String result = keyGenerator.generateKey("client1", "modelA", "");
    assertEquals("client1:modelA:tokens:", result);
  }

  @Test
  void generateKey_withAllEmptyInputs_returnsCorrectKey() {
    KeyGenerator keyGenerator = new KeyGenerator();
    String result = keyGenerator.generateKey("", "", "");
    assertEquals("::tokens:", result);
  }
}