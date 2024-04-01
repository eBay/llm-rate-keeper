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
}