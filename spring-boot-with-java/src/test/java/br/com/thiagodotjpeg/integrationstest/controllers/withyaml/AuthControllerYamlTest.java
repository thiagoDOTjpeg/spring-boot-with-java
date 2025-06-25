package br.com.thiagodotjpeg.integrationstest.controllers.withyaml;

import br.com.thiagodotjpeg.config.TestConfigs;
import br.com.thiagodotjpeg.integrationstest.controllers.withyaml.mapper.YAMLMapper;
import br.com.thiagodotjpeg.integrationstest.dto.AccountCredentialsDTO;
import br.com.thiagodotjpeg.integrationstest.dto.TokenDTO;
import br.com.thiagodotjpeg.integrationstest.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8888")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerYamlTest extends AbstractIntegrationTest {

  private static TokenDTO token;
  private static YAMLMapper yamlMapper;

  @BeforeAll
  static void setUp() {
    yamlMapper = new YAMLMapper();
    token =  new TokenDTO();
  }

  @Test
  @Order(1)
  void signIn() throws JsonProcessingException {
    AccountCredentialsDTO credentials = new AccountCredentialsDTO("leandro", "admin123");

    token = given()
            .config(RestAssuredConfig.config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
            .basePath("/auth/signin")
            .accept(MediaType.APPLICATION_YAML_VALUE)
            .port(TestConfigs.SERVER_PORT)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .body(credentials, yamlMapper)
            .when()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(TokenDTO.class, yamlMapper);



    assertNotNull(token.getAccessToken());
    assertNotNull(token.getRefreshToken());
    assertEquals("leandro", token.getUsername());
    assertTrue(token.getAuthenticated());
  }

  @Test
  @Order(2)
  void refreshToken() throws JsonProcessingException {
    token = given()
            .config(RestAssuredConfig.config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
            .basePath("/auth/refresh")
            .accept(MediaType.APPLICATION_YAML_VALUE)
            .port(TestConfigs.SERVER_PORT)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .pathParam("username", token.getUsername())
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getAccessToken())
            .when()
            .put("{username}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(TokenDTO.class, yamlMapper);

    assertNotNull(token.getAccessToken());
    assertNotNull(token.getRefreshToken());
    assertEquals("leandro", token.getUsername());
    assertTrue(token.getAuthenticated());
  }
}