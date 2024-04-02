package com.ebay.llm.qos.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;

public class ConfigLoader {

  public ModelClientConfig loadConfig(String fileName) throws IOException {
    if (fileName == null || fileName.isEmpty()) {
      throw new IllegalArgumentException("File name cannot be null or empty");
    }

    InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
    if (is == null) {
      throw new IOException("File does not exist: " + fileName);
    }

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    ModelClientConfig config = mapper.readValue(is, ModelClientConfig.class);

    if (config == null) {
      throw new IOException("Invalid config: " + fileName);
    }

    return config;
  }
}