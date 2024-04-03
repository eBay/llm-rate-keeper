package com.ebay.llm.qos.service;

import com.ebay.llm.qos.constant.TokenStoreEnum;
import com.ebay.llm.qos.store.TokenStore;
import com.ebay.llm.qos.store.nukv.NUKVTokenStore;
import com.ebay.llm.qos.store.redis.RedisTokenStore;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TokenStoreFactoryTest {

  @Test
  public void createTokenStore_returnsRedisTokenStore_whenRedisEnumIsPassed() {
    RedisClient redisClient = Mockito.mock(RedisClient.class);
    StatefulRedisConnection statefulRedisConnection = Mockito.mock(StatefulRedisConnection.class);
    Mockito.when(redisClient.connect()).thenReturn(statefulRedisConnection);
    TokenStore tokenStore = TokenStoreFactory.createTokenStore(TokenStoreEnum.REDIS, redisClient,
        true);
    Assertions.assertInstanceOf(RedisTokenStore.class, tokenStore);
  }

  @Test
  public void createTokenStore_returnsNUKVTokenStore_whenNUKVEnumIsPassed() {
    RedisClient redisClient = Mockito.mock(RedisClient.class);
    StatefulRedisConnection statefulRedisConnection = Mockito.mock(StatefulRedisConnection.class);
    Mockito.when(redisClient.connect()).thenReturn(statefulRedisConnection);
    TokenStore tokenStore = TokenStoreFactory.createTokenStore(TokenStoreEnum.NUKV, redisClient,
        true);
    Assertions.assertInstanceOf(NUKVTokenStore.class, tokenStore);
  }

  @Test
  public void createTokenStore_throwsIllegalArgumentException_whenInvalidEnumIsPassed() {
    RedisClient redisClient = Mockito.mock(RedisClient.class);
    Assertions.assertThrows(IllegalArgumentException.class, () ->
        TokenStoreFactory.createTokenStore(null, redisClient, true)
    );
  }

  @Test
  public void createTokenStore_returnsRedisTokenStore_whenRedisEnumIsPassedAndAsyncIsTrue() {
    RedisClient redisClient = Mockito.mock(RedisClient.class);
    StatefulRedisConnection statefulRedisConnection = Mockito.mock(StatefulRedisConnection.class);
    Mockito.when(redisClient.connect()).thenReturn(statefulRedisConnection);
    TokenStore tokenStore = TokenStoreFactory.createTokenStore(TokenStoreEnum.REDIS, redisClient,
        true);
    Assertions.assertInstanceOf(RedisTokenStore.class, tokenStore);
  }

  @Test
  public void createTokenStore_returnsRedisTokenStore_whenRedisEnumIsPassedAndAsyncIsFalse() {
    RedisClient redisClient = Mockito.mock(RedisClient.class);
    StatefulRedisConnection statefulRedisConnection = Mockito.mock(StatefulRedisConnection.class);
    Mockito.when(redisClient.connect()).thenReturn(statefulRedisConnection);
    TokenStore tokenStore = TokenStoreFactory.createTokenStore(TokenStoreEnum.REDIS, redisClient,
        false);
    Assertions.assertInstanceOf(RedisTokenStore.class, tokenStore);
  }

  @Test
  public void createTokenStore_returnsNUKVTokenStore_whenNUKVEnumIsPassedAndAsyncIsTrue() {
    RedisClient redisClient = Mockito.mock(RedisClient.class);
    StatefulRedisConnection statefulRedisConnection = Mockito.mock(StatefulRedisConnection.class);
    Mockito.when(redisClient.connect()).thenReturn(statefulRedisConnection);
    TokenStore tokenStore = TokenStoreFactory.createTokenStore(TokenStoreEnum.NUKV, redisClient,
        true);
    Assertions.assertInstanceOf(NUKVTokenStore.class, tokenStore);
  }

  @Test
  public void createTokenStore_returnsNUKVTokenStore_whenNUKVEnumIsPassedAndAsyncIsFalse() {
    RedisClient redisClient = Mockito.mock(RedisClient.class);
    StatefulRedisConnection statefulRedisConnection = Mockito.mock(StatefulRedisConnection.class);
    Mockito.when(redisClient.connect()).thenReturn(statefulRedisConnection);
    TokenStore tokenStore = TokenStoreFactory.createTokenStore(TokenStoreEnum.NUKV, redisClient,
        false);
    Assertions.assertInstanceOf(NUKVTokenStore.class, tokenStore);
  }
}