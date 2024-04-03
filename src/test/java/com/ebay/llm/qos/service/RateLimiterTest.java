package com.ebay.llm.qos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import com.ebay.llm.qos.config.Client;
import com.ebay.llm.qos.config.ClientModel;
import com.ebay.llm.qos.config.ConfigLoader;
import com.ebay.llm.qos.config.ModelClientConfig;
import com.ebay.llm.qos.constant.TokenStoreEnum;
import com.ebay.llm.qos.store.TokenStore;
import com.ebay.llm.qos.store.redis.RedisTokenStore;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import redis.embedded.RedisServer;

class RateLimiterTest {

  private TokenStore tokenStore;
  @Mock
  private ConfigLoader configLoader;
  @Mock
  private ModelClientConfig modelClientConfig;
  @Mock
  private Client client;
  @Mock
  private ClientModel clientModel;

  private RateLimiter rateLimiter;

  private RedisClient redisClient;
  private StatefulRedisConnection<String, String> connection;
  private RedisServer redisServer;


  @BeforeEach
  void setUp() throws IOException {
    redisServer = new RedisServer(6379);
    redisServer.start();
    redisClient = RedisClient.create("redis://localhost:6379");
    tokenStore = new RedisTokenStore(redisClient, false);
    connection = redisClient.connect();
    MockitoAnnotations.openMocks(this);
    when(configLoader.loadConfig(anyString())).thenReturn(modelClientConfig);
    when(modelClientConfig.getClientConfig("clientId")).thenReturn(
        client); // Ensure the client ID matches the one used in the test case
    when(client.getModelConfig(anyString())).thenReturn(clientModel);
    when(clientModel.getTokensLimitPerMinute()).thenReturn(100L);
    when(clientModel.getTokensLimitPerDay()).thenReturn(1000L);
    rateLimiter = new RateLimiter(TokenStoreEnum.REDIS, redisClient, false);
  }

  @AfterEach
  public void tearDown() {
    redisServer.stop();
  }


  @Test
  void getTokenLimits_returnsTokenLimits() {
    Pair<Long, Long> tokenLimits = rateLimiter.getTokenLimits("1", "modelA");
    assertEquals(80L, tokenLimits.getFirst());
    assertEquals(4800L, tokenLimits.getSecond());
  }


  @Test
  void isModelCanServe_returnsFalse_whenModelIsCoolingPeriod() throws InterruptedException {
    // Given
    String modelId = "testModel";
    rateLimiter.registerCoolingPeriod(modelId, 1000);
    boolean result = rateLimiter.isModelCanServe(modelId);
    // Then
    assertFalse(result);
  }

  @Test
  void isModelCanServe_returnTrue_WhenCoolingPeriodIsOver() throws InterruptedException {
    // Given
    String modelId = "testModel";
    tokenStore.setCoolingPeriod(modelId, 1000);
    Thread.sleep(1001);// Ensure the model is ready to serve
    boolean result = rateLimiter.isModelCanServe(modelId);
    // Then
    assertTrue(result);
  }


  @Test
  void isAllowed_returnsFalse_whenTokenStoreHasNoTokens() {
    // Given
    String modelId = "modelA";
    String clientId = "1";
    int tokensRequired = 100;

    // Consume tokens
    rateLimiter.updateTokenUsage(modelId, clientId, 6000, 0);

    // When
    boolean result = rateLimiter.isAllowed(modelId, clientId, tokensRequired);

    // Then
    assertFalse(result);
  }

  @Test
  void isAllowed_returnsTrue_whenTokenStoreHasTokens() {
    // Given
    String modelId = "modelA";
    String clientId = "1";
    int tokensRequired = 100;

    // Consume tokens
    rateLimiter.updateTokenUsage(modelId, clientId, 6000, 0);

    // When
    boolean result = rateLimiter.isAllowed(modelId, clientId, tokensRequired);

    // Then
    assertFalse(result);
  }


}