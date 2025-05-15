package br.com.thiagodotjpeg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageConfig {
  private String uploadDir;

  public FileStorageConfig() {
  }

  public String getUploadDir() {
    return uploadDir;
  }

  public void setUploadDir(String uploadDir) {
    this.uploadDir = uploadDir;
  }
}
