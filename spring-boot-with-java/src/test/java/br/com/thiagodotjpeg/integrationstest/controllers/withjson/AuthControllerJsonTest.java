package br.com.thiagodotjpeg.integrationstest.controllers.withjson;

import br.com.thiagodotjpeg.config.TestConfigs;
import br.com.thiagodotjpeg.integrationstest.dto.AccountCredentialsDTO;
import br.com.thiagodotjpeg.integrationstest.dto.TokenDTO;
import br.com.thiagodotjpeg.integrationstest.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8888")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerJsonTest extends AbstractIntegrationTest {

  private static TokenDTO token;

  @BeforeAll
  static void setUp() {
    token =  new TokenDTO();
  }

  @Test
  @Order(1)
  void signIn() {
    AccountCredentialsDTO credentials = new AccountCredentialsDTO("leandro", "admin123");

    token = given()
            .basePath("/auth/signin")
            .port(TestConfigs.SERVER_PORT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(credentials)
            .when()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(TokenDTO.class);

    assertNotNull(token.getAccessToken());
    assertNotNull(token.getRefreshToken());

  }

  @Test
  @Order(2)
  void refreshToken() {
    token = given()
            .basePath("/auth/refresh")
            .port(TestConfigs.SERVER_PORT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("username", token.getUsername())
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getAccessToken())
            .when()
            .put("{username}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(TokenDTO.class);

    assertNotNull(token.getAccessToken());
    assertNotNull(token.getRefreshToken());
  }
}