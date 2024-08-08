package com.ebay.llm.qos.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ConfigLoaderTest {

  private final ConfigLoader configLoader = new ConfigLoader();
  @TempDir
  Path tempDir;

  @Test
  void shouldLoadValidConfig() throws IOException {
    ModelClientConfig config = configLoader.loadConfig(
        "model-client-config.yml");
    assertEquals(6000, config.getGlobalSettings().getTokensLimitPerDay());
  }

  @Test
  void shouldThrowExceptionWhenFileDoesNotExist() {
    assertThrows(IOException.class, () -> configLoader.loadConfig("nonexistentFile.yaml"));
  }

  @Test
  void shouldThrowExceptionWhenConfigIsNull() throws IOException {
    String fileName = tempDir.resolve("nullConfig.yaml").toString();
    Files.createFile(Path.of(fileName));
    assertThrows(IOException.class, () -> configLoader.loadConfig(fileName));
  }

  @Test
  void shouldThrowExceptionWhenFileNameIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> configLoader.loadConfig(""));
  }
}