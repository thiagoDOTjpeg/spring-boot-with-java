package br.com.thiagodotjpeg.integrationstest.testcontainers;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.Map;
import java.util.stream.Stream;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:9.1.0");

    private void startContainer() {
      Startables.deepStart(Stream.of(mysql)).join();
    }

    private Map<String, String> createConnectionConfiguration() {
      return Map.of(
              "spring.datasource.url", mysql.getJdbcUrl(),
              "spring.datasource.username", mysql.getUsername(),
              "spring.datasource.password", mysql.getPassword()
      );
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      startContainer();
      ConfigurableEnvironment environment = applicationContext.getEnvironment();
      MapPropertySource testcontainers = new MapPropertySource("testcontainers",
              (Map) createConnectionConfiguration());
      environment.getPropertySources().addFirst(testcontainers);
    }

  }
}
