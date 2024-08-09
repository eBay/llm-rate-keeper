package com.ebay.llm.qos.store.redis.it;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ebay.llm.qos.model.ConsumedTokens;
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
    redisServer = new RedisServer(6379);
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
    assertTrue(redisTokenStore.hasTokens("testClient", "testModel", 100, 1000));
  }

  @Test
  public void testConsumeTokens() {
    redisTokenStore.consumeTokens("testClient", "testModel", 10, 100, 1000);
    assertTrue(redisTokenStore.hasTokens("testClient", "testModel", 100, 1000));
  }

  @Test
  public void testSetCoolingPeriod() {
    redisTokenStore.setCoolingPeriod("testModel", 60000);
    assertFalse(redisTokenStore.isModelReadyToServe("testModel"));
  }

  @Test
  public void testIsModelReadyToServe() {
    redisTokenStore.setCoolingPeriod("testModel", 60000);
    assertFalse(redisTokenStore.isModelReadyToServe("testModel"));
  }

  @Test
  public void testResetCoolingPeriod() {
    redisTokenStore.setCoolingPeriod("testModel", 60000);
    redisTokenStore.resetCoolingPeriod("testModel");
    assertTrue(redisTokenStore.isModelReadyToServe("testModel"));
  }

  @Test
  public void shouldReturnTrueWhenClientHasTokens() {
    assertTrue(redisTokenStore.hasTokens("testClient", "testModel", 100, 1000));
  }

  @Test
  public void shouldReturnTrueAfterConsumingTokens() {
    redisTokenStore.consumeTokens("testClient", "testModel", 10, 100, 1000);
    assertTrue(redisTokenStore.hasTokens("testClient", "testModel", 100, 1000));
  }

  @Test
  public void shouldReturnFalseWhenModelIsNotReadyToServe() {
    redisTokenStore.setCoolingPeriod("testModel", 60000);
    assertFalse(redisTokenStore.isModelReadyToServe("testModel"));
  }

  @Test
  public void shouldReturnTrueWhenCoolingPeriodIsReset() {
    redisTokenStore.setCoolingPeriod("testModel", 60000);
    redisTokenStore.resetCoolingPeriod("testModel");
    assertTrue(redisTokenStore.isModelReadyToServe("testModel"));
  }

  @Test
  public void testHasTokensWithAsync() {
    redisTokenStore = new RedisTokenStore(redisClient, true);
    assertTrue(redisTokenStore.hasTokens("testClient", "testModel", 100, 1000));
  }

  @Test
  public void testConsumeTokensWithAsync() {
    redisTokenStore = new RedisTokenStore(redisClient, true);
    ConsumedTokens consumedTokens = redisTokenStore.consumeTokens("testClient", "testModel", 10,
        100, 1000);
    assertNotNull(consumedTokens);
    assertTrue(consumedTokens.getPerMinTokens() > 0);
    assertTrue(consumedTokens.getPerDayTokens() > 0);
    assertTrue(redisTokenStore.hasTokens("testClient", "testModel", 100, 1000));
  }
}