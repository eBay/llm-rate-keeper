# LLMRateKeeper 
LLMRateKeeper is a Java library designed to integrate token rate limiting into applications using Generative AI models to maintain Quality of Service (QoS). It leverages Redis as a backend for token storage and provides a configurable system to manage API and token usage limits for different models and clients.

**Summary of Configuration Settings:**

**Global Settings:** Defines the Redis key-value store name, global API call limits per minute, token limits per minute and per day, as well as a standard cooling period duration in seconds after which rate-limited clients or models can resume normal operations.

**Models Configuration:** Allows for the definition of various models, each with its own description, API call limits, token limits per request, per minute, and a specific cooling period. These models are tailored to cater to different types of clients, such as premium clients that may require higher capacities.

**Default Client Token Limits:** Sets default token limits for clients based on the global settings, with a specified number of tokens allowed per minute and per day.

**Clients Configuration:** Specifies individual clients by ID, name, and description, and assigns them to models. Each client-model pairing can have customized token limits per minute and per day. Clients can also have a fallback model to use if necessary.

To use LLMRateKeeper, developers need to add the Maven dependency to their project, create a model-client-config.yml configuration file, and utilize the provided TokenRateLimiter methods in their code to update token usage, check token limits, update and reset model cooling periods, and determine if a model is ready to serve requests.

Overall, LLMRateKeeper provides a structured and manageable way to enforce rate limiting, ensuring that clients adhere to specified usage limits and that services maintain optimal performance levels without being overwhelmed by excessive requests.

## Usage Guide

This guide provides instructions on how to use the LLMRateKeeper in your Java applications. Follow the steps below to integrate the LLMRateKeeper using Maven, configure your models and clients, and utilize the provided methods in your code.

## Step 1: Add Maven Dependency

Include the following dependency in your project's `pom.xml` file to use the Redis based token rate limiter:

```xml
<dependency>
    <groupId>com.ebay.llm</groupId>
    <artifactId>llm-rate-keeper</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Step 2: Configuration File

Create a `model-client-config.yml` file under the `src/main/resources` directory. Add the configuration for your model and client as shown below:

```yaml
globalSettings:
  redisKVStore: "ChatAppTokenCountKVStore"
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

## Step 3: Use LLMRateKeeper in Code

Inject the `TokenStore` and create an instance of `TokenRateLimiter` in your code as follows:

```java

private TokenStore redisTokenStore;
private RedisClient redisClient;
redisClient = RedisClient.create("redis://localhost:6379");
redisTokenStore = new RedisTokenStore(redisClient);
TokenRateLimiter tokenRateLimiter = new TokenRateLimiter(redisTokenStore);
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

## Contributing to This Project
We welcome contributions. If you find any bugs, potential flaws and edge cases, improvements, new feature suggestions or discussions, please submit issues or pull requests.

# Contact
- Praba Karuppaiah (pkaruppaiah@ebay.com)
- Ramesh Periyathambi (rperiyathambi@ebay.com)

## License Information
Copyright 2023-2024 eBay Inc.

Authors/Developers: Praba Karuppaiah, Ramesh Periyathambi

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
