package com.ebay.llm.qos.config;

import java.util.List;
import lombok.Data;

@Data
public class Client {

  private String id;
  private String name;
  private String description;
  private List<ClientModel> models;
  private RateLimits rateLimits;


  public ClientModel getModelConfig(String modelId) {
    return models.stream()
        .filter(model -> model.getId().equals(modelId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid model ID: " + modelId));
  }
}