package com.ebay.llm.qos.store.redis;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    MockitoAnnotations.openMocks(this);
    when(mockRedisClient.connect()).thenReturn(mockConnection);
    when(mockConnection.sync()).thenReturn(mockSyncCommands);
    redisTokenStore = new RedisTokenStore(mockRedisClient, false);
  }

  @Test
  void testHasTokens() {
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
    Assertions.assertTrue(result);
    verify(mockSyncCommands).get(minuteKey);
    verify(mockSyncCommands).get(dayKey);
  }

  @Test
  void testConsumeTokens() {
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
  void testShutdown() {
    // Act
    redisTokenStore.shutdown();

    // Assert
    verify(mockConnection).close();
    verify(mockRedisClient).shutdown();
  }
}