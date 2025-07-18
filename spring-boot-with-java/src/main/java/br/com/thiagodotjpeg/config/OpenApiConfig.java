package br.com.thiagodotjpeg.config;

import br.com.thiagodotjpeg.services.InstanceInformationService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Autowired
    InstanceInformationService instanceInformationService;

  @Bean
  OpenAPI customOpenAPI() {

    return new OpenAPI()
            .info(new Info()
                    .title("REST API's RESTful from 0 with Java, Spring Boot, Kubernetes and Docker V2 " + instanceInformationService.retrieveInstanceInfo())
                    .version("v1")
                    .description("REST API's RESTful from 0 with Java, Spring Boot, Kubernetes and Docker")
                    .termsOfService("https://github.com/thiagodotjpeg")
                    .license(new License()
                            .name("Apache 2.0")
                            .url("https://github.com/thiagodotjpeg")));
  }
}
