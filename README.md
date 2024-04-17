# llm-token-ratelimiter
# Jenkins job to build and deploy the llm-token-ratelimiter artifact to maven repository
https://ci-lvsaz02.cloud.ebay.com/databtch-8405/job/llm-token-ratelimiter/

```markdown
# Redis Based Token Limiter Usage Guide

This guide provides instructions on how to use the Redis based token limiter in your Java applications. Follow the steps below to integrate the token rate limiter using Maven, configure your models and clients, and utilize the provided methods in your code.

## Step 1: Add Maven Dependency

Include the following dependency in your project's `pom.xml` file to use the Redis based token rate limiter:

```xml
<dependency>
    <groupId>com.ebay.llm</groupId>
    <artifactId>Redis-llm-token-ratelimiter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Step 2: Configuration File

Create a `model-client-config.yml` file under the `src/main/resources` directory. Add the configuration for your model and client as shown below:

```yaml
globalSettings:
  RedisHost: "hubgptgatewaytokencountRedishost"
  apiCallsLimitPerMinute: 60
  tokensLimitPerMinute: 100
  tokensLimitPerDay: 6000
  coolingPeriodSeconds: 60 # Duration in seconds for the cooling period

models:
  - id: "modelA"
    description: "High-capacity model for premium clients"
    apiLimitPerMinute: 60
    tokensLimitPerRequest: 1000
    tokensLimitPerMinute: 80
    coolingPeriodSeconds: 60
  # Add additional models as needed

defaultClientTokenLimits:
  tokensLimitPerMinute: 100 # Value from globalSettings
  tokensLimitPerDay: 6000   # Value from globalSettings

clients:
  - id: "1"
    name: "buyer-app"
    description: "Client for the buyer application"
    models:
      - id: "modelA"
        tokensLimitPerMinute: 80
        tokensLimitPerDay: 4800
        fallback: "modelB"
      # Add additional clients and models as needed
```

## Step 3: Store Configuration in Redis

To store the configuration in Redis, create a Redis keyspace and set the `RedisHost` as shown in the configuration file.

## Step 4: Use Token Limiter in Code

Inject the `TokenStore` and create an instance of `TokenRateLimiter` in your code as follows:

```java
@Inject
private TokenStore RedisTokenStore;

TokenRateLimiter tokenRateLimiter = new TokenRateLimiter(RedisTokenStore);
```

Now you can start using the methods provided by the `TokenRateLimiter`.

### Update Token Usage

Update the token usage for a specific client and model:

```java
tokenRateLimiter.updateTokenUsage(String clientId, String modelId, long tokensUsed);
```

### Check Token Limit

Check if the client has exceeded the token limit:

```java
boolean isAllowed = tokenRateLimiter.isAllowed(String clientId, String modelId, long tokensRequested);
```

### Update Model Cooling Period

Update the cooling period for a model if it is rate-limited:

```java
tokenRateLimiter.updateModelCoolingPeriod(String modelId, long coolingPeriodSeconds);
```

### Check Model Readiness

Check if a model is ready to serve and not rate-limited:

```java
boolean isModelReady = tokenRateLimiter.isModelReady(String modelId);
```

### Reset Model Cooling Period

Reset the cooling period for a model:

```java
tokenRateLimiter.resetModelCoolingPeriod(String modelId);
```

By following these steps, you can effectively integrate and manage token rate limiting in your applications using the Redis based token limiter.
```
