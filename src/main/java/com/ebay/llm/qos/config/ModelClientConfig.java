package com.ebay.llm.qos.config;

import java.util.List;
import lombok.Data;

@Data
public class ModelClientConfig {

  private GlobalSettings globalSettings;
  private List<Model> models;
  private DefaultClientTokenLimits defaultClientTokenLimits;
  private List<Client> clients;
}