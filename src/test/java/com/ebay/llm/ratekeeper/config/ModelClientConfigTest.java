package com.ebay.llm.ratekeeper.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;


public class ModelClientConfigTest {

  private ModelClientConfig modelClientConfig;

  @BeforeEach
  public void setUp() {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    try {
      modelClientConfig = mapper.readValue(
          Path.of("src/test/resources/model-client-config.yml").toFile(), ModelClientConfig.class);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void shouldLoadCorrectGlobalSettings() {
    assertEquals(60, modelClientConfig.getGlobalSettings().getApiCallsLimitPerMinute());
    assertEquals(100, modelClientConfig.getGlobalSettings().getTokensLimitPerMinute());
    assertEquals(6000, modelClientConfig.getGlobalSettings().getTokensLimitPerDay());
    assertEquals(60, modelClientConfig.getGlobalSettings().getCoolingPeriodSeconds());
  }

  @Test
  void shouldLoadCorrectModels() {
    assertEquals(3, modelClientConfig.getModels().size());
    assertEquals("modelA", modelClientConfig.getModels().get(0).getId());
    assertEquals("modelB", modelClientConfig.getModels().get(1).getId());
    assertEquals("modelC", modelClientConfig.getModels().get(2).getId());
  }

  @Test
  void shouldLoadCorrectDefaultClientTokenLimits() {
    assertEquals(100, modelClientConfig.getDefaultClientTokenLimits().getTokensLimitPerMinute());
    assertEquals(6000, modelClientConfig.getDefaultClientTokenLimits().getTokensLimitPerDay());
  }


  @Test
  void shouldThrowExceptionWhenClientIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> modelClientConfig.getClientConfig(null));
  }

  @Test
  void shouldLoadCorrectClients() {
    assertEquals(1, modelClientConfig.getClients().size());
    Client client = modelClientConfig.getClients().get(0);
    assertEquals("1", client.getId());
    assertEquals("buyer-app", client.getName());
    assertEquals("Client for the buyer application", client.getDescription());
    assertEquals(3, client.getModels().size());
    assertEquals("modelA", client.getModels().get(0).getId());
    assertEquals("modelB", client.getModels().get(1).getId());
    assertEquals("modelC", client.getModels().get(2).getId());
  }
}