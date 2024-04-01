package com.ebay.llm.qos.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ConfigLoaderTest {

  @TempDir
  Path tempDir;

  @Test
  void shouldLoadValidConfig() throws IOException {
    ConfigLoader configLoader = new ConfigLoader();
    ModelClientConfig config = configLoader.loadConfig(
        "src/test/resources/model-client-config.yml");
    assertEquals(6000, config.getGlobalSettings().getTokensLimitPerDay());
  }

  @Test
  void shouldThrowExceptionWhenFileDoesNotExist() {
    ConfigLoader configLoader = new ConfigLoader();
    assertThrows(IOException.class, () -> configLoader.loadConfig("nonexistentFile.yaml"));
  }

  @Test
  void shouldThrowExceptionWhenConfigIsNull() throws IOException {
    String fileName = tempDir.resolve("nullConfig.yaml").toString();
    Files.createFile(Path.of(fileName));
    ConfigLoader configLoader = new ConfigLoader();
    assertThrows(IOException.class, () -> configLoader.loadConfig(fileName));
  }
}
