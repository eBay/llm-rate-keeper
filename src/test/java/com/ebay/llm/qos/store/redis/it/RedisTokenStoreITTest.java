package com.ebay.llm.qos.store.redis.it;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ebay.llm.qos.store.redis.RedisTokenStore;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.embedded.RedisServer;

public class RedisTokenStoreITTest {

  private RedisTokenStore redisTokenStore;
  private RedisClient redisClient;
  private StatefulRedisConnection<String, String> connection;
  private RedisServer redisServer;

  @BeforeEach
  public void setup() throws IOException {
    redisServer = new RedisServer(6379);  // or any free port
    redisServer.start();
    redisClient = RedisClient.create("redis://localhost:6379");
    redisTokenStore = new RedisTokenStore(redisClient, false);
    connection = redisClient.connect();
  }

  @AfterEach
  public void tearDown() {
    redisServer.stop();
  }

  @Test
  public void testHasTokens() {
    // Setup
    String clientId = "testClient";
    String modelId = "testModel";
    long tokensPerMinuteLimit = 100;
    long tokensPerDayLimit = 1000;

    // Execute
    boolean result = redisTokenStore.hasTokens(clientId, modelId, tokensPerMinuteLimit,
        tokensPerDayLimit);

    // Validate
    assertTrue(result, "Expected true when checking if client has tokens");
  }

  @Test
  public void testConsumeTokens() {
    // Setup
    String clientId = "testClient";
    String modelId = "testModel";
    long tokens = 10;
    long tokensPerMinuteLimit = 100;
    long tokensPerDayLimit = 1000;

    // Execute
    redisTokenStore.consumeTokens(clientId, modelId, tokens, tokensPerMinuteLimit,
        tokensPerDayLimit);

    // Validate
    assertTrue(
        redisTokenStore.hasTokens(clientId, modelId, tokensPerMinuteLimit, tokensPerDayLimit),
        "Expected true when checking if client has tokens after consuming tokens");
  }

  @Test
  public void testSetCoolingPeriod() {
    // Setup
    String modelId = "testModel";
    int durationInMilliSeconds = 60000; // 1 minute

    // Execute
    redisTokenStore.setCoolingPeriod(modelId, durationInMilliSeconds);

    // Validate
    assertFalse(redisTokenStore.isModelReadyToServe(modelId),
        "Expected false when checking if model is ready to serve after setting cooling period");
  }

  @Test
  public void testIsModelReadyToServe() {
    // Setup
    String modelId = "testModel";
    int durationInMilliSeconds = 60000; // 1 minute
    redisTokenStore.setCoolingPeriod(modelId, durationInMilliSeconds);

    // Execute
    boolean result = redisTokenStore.isModelReadyToServe(modelId);

    // Validate
    assertFalse(result, "Expected false when checking if model is ready to serve");
  }

  @Test
  public void testResetCoolingPeriod() {
    // Setup
    String modelId = "testModel";
    int durationInMilliSeconds = 60000; // 1 minute
    redisTokenStore.setCoolingPeriod(modelId, durationInMilliSeconds);

    // Execute
    redisTokenStore.resetCoolingPeriod(modelId);

    // Validate
    assertTrue(redisTokenStore.isModelReadyToServe(modelId),
        "Expected true when checking if model is ready to serve after resetting cooling period");
  }

  @Test
  public void shouldReturnTrueWhenClientHasTokens() {
    // Given
    String clientId = "testClient";
    String modelId = "testModel";
    long tokensPerMinuteLimit = 100;
    long tokensPerDayLimit = 1000;

    // When
    boolean result = redisTokenStore.hasTokens(clientId, modelId, tokensPerMinuteLimit,
        tokensPerDayLimit);

    // Then
    assertTrue(result);
  }

  @Test
  public void shouldReturnTrueAfterConsumingTokens() {
    // Given
    String clientId = "testClient";
    String modelId = "testModel";
    long tokens = 10;
    long tokensPerMinuteLimit = 100;
    long tokensPerDayLimit = 1000;

    // When
    redisTokenStore.consumeTokens(clientId, modelId, tokens, tokensPerMinuteLimit,
        tokensPerDayLimit);

    // Then
    assertTrue(
        redisTokenStore.hasTokens(clientId, modelId, tokensPerMinuteLimit, tokensPerDayLimit));
  }

  @Test
  public void shouldReturnFalseWhenModelIsNotReadyToServe() {
    // Given
    String modelId = "testModel";
    int durationInMilliSeconds = 60000; // 1 minute

    // When
    redisTokenStore.setCoolingPeriod(modelId, durationInMilliSeconds);

    // Then
    assertFalse(redisTokenStore.isModelReadyToServe(modelId));
  }

  @Test
  public void shouldReturnTrueWhenCoolingPeriodIsReset() {
    // Given
    String modelId = "testModel";
    int durationInMilliSeconds = 60000; // 1 minute
    redisTokenStore.setCoolingPeriod(modelId, durationInMilliSeconds);

    // When
    redisTokenStore.resetCoolingPeriod(modelId);

    // Then
    assertTrue(redisTokenStore.isModelReadyToServe(modelId));
  }

  @Test
  public void testHasTokensWithAsync() {
    // Setup
    redisTokenStore = new RedisTokenStore(redisClient, true);
    String clientId = "testClient";
    String modelId = "testModel";
    long tokensPerMinuteLimit = 100;
    long tokensPerDayLimit = 1000;

    // Execute
    boolean result = redisTokenStore.hasTokens(clientId, modelId, tokensPerMinuteLimit,
        tokensPerDayLimit);

    // Validate
    assertTrue(result, "Expected true when checking if client has tokens");
  }

  @Test
  public void testConsumeTokensWithAsync() {
    // Setup
    redisTokenStore = new RedisTokenStore(redisClient, true);
    String clientId = "testClient";
    String modelId = "testModel";
    long tokens = 10;
    long tokensPerMinuteLimit = 100;
    long tokensPerDayLimit = 1000;

    // Execute
    redisTokenStore.consumeTokens(clientId, modelId, tokens, tokensPerMinuteLimit,
        tokensPerDayLimit);

    // Validate
    assertTrue(
        redisTokenStore.hasTokens(clientId, modelId, tokensPerMinuteLimit, tokensPerDayLimit),
        "Expected true when checking if client has tokens after consuming tokens");
  }
}
