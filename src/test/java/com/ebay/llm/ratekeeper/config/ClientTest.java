package com.ebay.llm.ratekeeper.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClientTest {

  private Client client;

  @BeforeEach
  public void setUp() {
    client = new Client();
    client.setId("1");
    client.setName("buyer-app");
    client.setDescription("Client for the buyer application");

    ClientModel modelA = new ClientModel();
    modelA.setId("modelA");

    ClientModel modelB = new ClientModel();
    modelB.setId("modelB");

    ClientModel modelC = new ClientModel();
    modelC.setId("modelC");

    client.setModels(Arrays.asList(modelA, modelB, modelC));
  }

  @Test
  void shouldReturnCorrectModelConfigWhenModelIdExists() {
    ClientModel model = client.getModelConfig("modelA");
    assertEquals("modelA", model.getId());
  }

  @Test
  void shouldThrowExceptionWhenModelIdDoesNotExist() {
    assertThrows(IllegalArgumentException.class, () -> client.getModelConfig("nonexistentModelId"));
  }


  @Test
  void shouldReturnCorrectId() {
    assertEquals("1", client.getId());
  }

  @Test
  void shouldReturnCorrectName() {
    assertEquals("buyer-app", client.getName());
  }

  @Test
  void shouldReturnCorrectDescription() {
    assertEquals("Client for the buyer application", client.getDescription());
  }

  @Test
  void shouldThrowExceptionWhenModelIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> client.getModelConfig(null));
  }
}
