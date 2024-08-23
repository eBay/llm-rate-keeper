package com.ebay.llm.ratekeeper.store.redis;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import com.ebay.llm.ratekeeper.store.exception.TokenStoreException;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class RedisTokenStoreTest {

  @Mock
  private RedisClient mockRedisClient;

  @Mock
  private StatefulRedisConnection<String, String> mockConnection;

  @Mock
  private RedisCommands<String, String> mockSyncCommands;

  private RedisTokenStore redisTokenStore;

  @BeforeEach
  void setUp() {
    openMocks(this);
    when(mockRedisClient.connect()).thenReturn(mockConnection);
    when(mockConnection.sync()).thenReturn(mockSyncCommands);
    redisTokenStore = new RedisTokenStore(mockRedisClient, false);
  }

  @Test
  void hasTokens() {
    // Arrange
    String clientId = "client1";
    String modelId = "model1";
    long tokensPerMinuteLimit = 10;
    long tokensPerDayLimit = 100;
    String minuteKey = clientId + ":" + modelId + ":tokens:minute";
    String dayKey = clientId + ":" + modelId + ":tokens:day";
    when(mockSyncCommands.get(minuteKey)).thenReturn("5,1616161616161");
    when(mockSyncCommands.get(dayKey)).thenReturn("50,1616161616161");

    // Act
    boolean result = redisTokenStore.hasTokens(clientId, modelId, tokensPerMinuteLimit,
        tokensPerDayLimit);

    // Assert
    assertTrue(result);
    verify(mockSyncCommands).get(minuteKey);
    verify(mockSyncCommands).get(dayKey);
  }

  @Test
  void consumeTokens() {
    // Arrange
    String clientId = "client1";
    String modelId = "model1";
    long tokens = 5;
    long tokensPerMinuteLimit = 10;
    long tokensPerDayLimit = 100;
    String minuteKey = clientId + ":" + modelId + ":tokens:minute";
    String dayKey = clientId + ":" + modelId + ":tokens:day";
    when(mockSyncCommands.get(minuteKey)).thenReturn(null);
    when(mockSyncCommands.get(dayKey)).thenReturn(null);

    // Act
    redisTokenStore.consumeTokens(clientId, modelId, tokens, tokensPerMinuteLimit,
        tokensPerDayLimit);

    // Assert
    verify(mockSyncCommands).set(eq(minuteKey), anyString());
    verify(mockSyncCommands).set(eq(dayKey), anyString());
  }

  @Test
  void isModelReadyToServeWhenNotInCoolingPeriod() {
    // Arrange
    String modelId = "model1";
    when(mockSyncCommands.get(eq("cooling:" + modelId))).thenReturn(null);

    // Act
    boolean result = redisTokenStore.isModelReadyToServe(modelId);

    // Assert
    assertTrue(result);
  }

  @Test
  void resetCoolingPeriod() {
    // Arrange
    String modelId = "model1";

    // Act
    redisTokenStore.resetCoolingPeriod(modelId);

    // Assert
    verify(mockSyncCommands).del(eq("cooling:" + modelId));
  }

  @Test
  void setCoolingPeriod() {
    // Arrange
    String modelId = "model1";
    int durationInMilliseconds = 60000;

    // Act
    redisTokenStore.setCoolingPeriod(modelId, durationInMilliseconds);

    // Assert
    verify(mockSyncCommands).setex(eq("cooling:" + modelId), eq(60L), eq("true"));
  }

  @Test
  void isModelReadyToServeWhenInCoolingPeriod() {
    // Arrange
    String modelId = "model1";
    when(mockSyncCommands.get(eq("cooling:" + modelId))).thenReturn("true");

    // Act
    boolean result = redisTokenStore.isModelReadyToServe(modelId);

    // Assert
    assertFalse(result);
  }

  @Test
  void consumeThrowsException() {
    // Arrange
    String clientId = "client1";
    String modelId = "model1";
    long tokens = 5;
    long tokensPerMinuteLimit = 10;
    long tokensPerDayLimit = 100;
    doThrow(new RuntimeException("Redis error")).when(mockSyncCommands)
        .set(anyString(), anyString());

    // Act and Assert
    assertThrows(TokenStoreException.class, () -> {
      redisTokenStore.consumeTokens(clientId, modelId, tokens, tokensPerMinuteLimit,
          tokensPerDayLimit);
    });
  }

  @Test
  void hasTokensReturnsTrueWhenEnoughTokens() {
    when(mockSyncCommands.get("testClient:testModel:minute")).thenReturn("100");
    when(mockSyncCommands.get("testClient:testModel:day")).thenReturn("1000");

    boolean result = redisTokenStore.hasTokens("testClient", "testModel", 200, 2000);

    assertTrue(result);
  }

  @Test
  void setCoolingPeriodSetsCoolingPeriod() {
    redisTokenStore.setCoolingPeriod("testModel", 1000);

    // Verify that setex was called with the correct parameters
    verify(mockSyncCommands).setex(eq("cooling:testModel"), eq(1L), eq("true"));

    boolean result = redisTokenStore.isModelReadyToServe("testModel");

    assertTrue(result);
  }

  @Test
  void resetCoolingPeriodResetsCoolingPeriod() {
    redisTokenStore.setCoolingPeriod("testModel", 1000);
    redisTokenStore.resetCoolingPeriod("testModel");

    boolean result = redisTokenStore.isModelReadyToServe("testModel");

    assertTrue(result);
  }

  @Test
  void shutdownClosesConnectionAndClient() {
    redisTokenStore.shutdown();

    // Verify that the connection and client are closed after shutdown
    verify(mockConnection).close();
    verify(mockRedisClient).shutdown();
  }

  @Test
  void consumeTokensThrowsExceptionWhenRedisErrorOccurs() {
    String clientId = "client1";
    String modelId = "model1";
    long tokens = 5;
    long tokensPerMinuteLimit = 10;
    long tokensPerDayLimit = 100;
    doThrow(new RuntimeException("Redis error")).when(mockSyncCommands)
        .set(anyString(), anyString());

    assertThrows(TokenStoreException.class, () -> {
      redisTokenStore.consumeTokens(clientId, modelId, tokens, tokensPerMinuteLimit,
          tokensPerDayLimit);
    });
  }

  @Test
  void isModelReadyToServeThrowsExceptionWhenRedisErrorOccurs() {
    String modelId = "model1";
    when(mockSyncCommands.get(eq("cooling:" + modelId))).thenThrow(
        new RuntimeException("Redis error"));

    assertThrows(TokenStoreException.class, () -> {
      redisTokenStore.isModelReadyToServe(modelId);
    });
  }

  @Test
  void resetCoolingPeriodThrowsExceptionWhenRedisErrorOccurs() {
    String modelId = "model1";
    doThrow(new RuntimeException("Redis error")).when(mockSyncCommands)
        .del(eq("cooling:" + modelId));

    assertThrows(TokenStoreException.class, () -> {
      redisTokenStore.resetCoolingPeriod(modelId);
    });
  }

  @Test
  void setCoolingPeriodThrowsExceptionWhenRedisErrorOccurs() {
    String modelId = "model1";
    int durationInMilliseconds = 60000;
    doThrow(new RuntimeException("Redis error")).when(mockSyncCommands)
        .setex(eq("cooling:" + modelId), eq(60L), eq("true"));

    assertThrows(TokenStoreException.class, () -> {
      redisTokenStore.setCoolingPeriod(modelId, durationInMilliseconds);
    });
  }
}