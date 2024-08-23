package com.ebay.llm.ratekeeper.config;

import java.util.List;
import lombok.Data;

@Data
public class ModelClientConfig {

  private GlobalSettings globalSettings;
  private List<Model> models;
  private DefaultClientTokenLimits defaultClientTokenLimits;
  private List<Client> clients;

  public Client getClientConfig(String clientId) {
    return clients.stream()
        .filter(client -> client.getId().equals(clientId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid client ID: " + clientId));
  }
}