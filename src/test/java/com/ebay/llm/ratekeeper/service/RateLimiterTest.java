package com.ebay.llm.ratekeeper.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import com.ebay.llm.ratekeeper.config.Client;
import com.ebay.llm.ratekeeper.config.ClientModel;
import com.ebay.llm.ratekeeper.config.ConfigLoader;
import com.ebay.llm.ratekeeper.config.ModelClientConfig;
import com.ebay.llm.ratekeeper.store.TokenStore;
import com.ebay.llm.ratekeeper.store.redis.RedisTokenStore;
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
    when(modelClientConfig.getClientConfig("clientId")).thenReturn(client);
    when(client.getModelConfig(anyString())).thenReturn(clientModel);
    when(clientModel.getTokensLimitPerMinute()).thenReturn(100L);
    when(clientModel.getTokensLimitPerDay()).thenReturn(1000L);
    TokenStore tokenStore = new RedisTokenStore(redisClient, false);
    rateLimiter = new RateLimiter(tokenStore);
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
    Thread.sleep(1001);
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
  void isAllowed_returnsFalse_whenTokenStoreHasTokens() {
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
  void isAllowed_returnsTrue_whenTokenStoreHasSufficientTokens() {
    // Given
    String modelId = "modelA";
    String clientId = "1";
    int tokensRequired = 50;

    // Ensure tokens are available
    rateLimiter.updateTokenUsage(modelId, clientId, 0, 0);

    // When
    boolean result = rateLimiter.isAllowed(modelId, clientId, tokensRequired);

    // Then
    assertTrue(result);
  }

  @Test
  void isAllowed_returnsFalse_whenTokenStoreHasInsufficientTokens() {
    // Given
    String modelId = "modelA";
    String clientId = "1";
    int tokensRequired = 2000;

    // Consume tokens
    rateLimiter.updateTokenUsage(modelId, clientId, 6000, 0);

    // When
    boolean result = rateLimiter.isAllowed(modelId, clientId, tokensRequired);

    // Then
    assertFalse(result);
  }

  @Test
  void getTokenLimits_returnsCorrectTokenLimits() {
    // Given
    String clientId = "1";
    String modelId = "modelA";

    // When
    Pair<Long, Long> tokenLimits = rateLimiter.getTokenLimits(clientId, modelId);

    // Then
    assertEquals(80L, tokenLimits.getFirst());
    assertEquals(4800L, tokenLimits.getSecond());
  }

  @Test
  void registerCoolingPeriod_setsCoolingPeriodCorrectly() {
    // Given
    String modelId = "testModel";
    long coolDownPeriodInSeconds = 2;

    // When
    rateLimiter.registerCoolingPeriod(modelId, coolDownPeriodInSeconds);

    // Then
    assertFalse(rateLimiter.isModelCanServe(modelId));
  }

  @Test
  void isModelCanServe_returnsTrue_afterCoolingPeriod() throws InterruptedException {
    // Given
    String modelId = "testModel";
    long coolDownPeriodInSeconds = 1;

    // When
    rateLimiter.registerCoolingPeriod(modelId, coolDownPeriodInSeconds);
    Thread.sleep(1001);

    // Then
    assertTrue(rateLimiter.isModelCanServe(modelId));
  }
}