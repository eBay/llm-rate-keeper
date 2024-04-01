package com.ebay.llm.qos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigLoader {

  public ModelClientConfig loadConfig(String fileName) throws IOException {
    if (fileName == null || fileName.isEmpty()) {
      throw new IllegalArgumentException("File name cannot be null or empty");
    }

    if (!Files.exists(Paths.get(fileName))) {
      throw new IOException("File does not exist: " + fileName);
    }

    if (!Files.isReadable(Paths.get(fileName))) {
      throw new IOException("File cannot be read: " + fileName);
    }

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    ModelClientConfig config = mapper.readValue(new File(fileName), ModelClientConfig.class);

    // Validate the config
    // Assuming validation is done by checking if config is not null
    if (config == null) {
      throw new IOException("Invalid config: " + fileName);
    }

    return config;
  }
}