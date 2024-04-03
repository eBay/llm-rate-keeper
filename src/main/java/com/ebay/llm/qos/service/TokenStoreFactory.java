package com.ebay.llm.qos.service;

import com.ebay.llm.qos.constant.TokenStoreEnum;
import com.ebay.llm.qos.store.TokenStore;
import com.ebay.llm.qos.store.nukv.NUKVTokenStore;
import com.ebay.llm.qos.store.redis.RedisTokenStore;
import io.lettuce.core.RedisClient;

public class TokenStoreFactory {

  public static TokenStore createTokenStore(TokenStoreEnum tokenStoreEnum, RedisClient redisClient,
      boolean isAsync) {
    if (tokenStoreEnum == null) {
      throw new IllegalArgumentException("TokenStoreEnum type cannot be null");
    }
    switch (tokenStoreEnum) {
      case REDIS:
        return new RedisTokenStore(redisClient, isAsync);
      case NUKV:
        return new NUKVTokenStore();
    }
    return null;
  }
}