package br.com.thiagodotjpeg.integrationstest.controllers.withxml;

import br.com.thiagodotjpeg.config.TestConfigs;
import br.com.thiagodotjpeg.integrationstest.dto.AccountCredentialsDTO;
import br.com.thiagodotjpeg.integrationstest.dto.TokenDTO;
import br.com.thiagodotjpeg.integrationstest.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8888")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerXmlTest extends AbstractIntegrationTest {

  private static TokenDTO token;
  private static XmlMapper xmlMapper;

  @BeforeAll
  static void setUp() {
    xmlMapper = new XmlMapper();
    token =  new TokenDTO();
  }

  @Test
  @Order(1)
  void signIn() throws JsonProcessingException {
    AccountCredentialsDTO credentials = new AccountCredentialsDTO("leandro", "admin123");

    var content = given()
            .basePath("/auth/signin")
            .accept(MediaType.APPLICATION_XML_VALUE)
            .port(TestConfigs.SERVER_PORT)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .body(credentials)
            .when()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString();


    TokenDTO tokenDTO = xmlMapper.readValue(content, TokenDTO.class);
    token =  tokenDTO;

    assertNotNull(token.getAccessToken());
    assertNotNull(token.getRefreshToken());

    assertEquals("leandro", tokenDTO.getUsername());
    assertTrue(tokenDTO.getAuthenticated());
  }

  @Test
  @Order(2)
  void refreshToken() throws JsonProcessingException {
    var content = given()
            .basePath("/auth/refresh")
            .accept(MediaType.APPLICATION_XML_VALUE)
            .port(TestConfigs.SERVER_PORT)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .pathParam("username", token.getUsername())
            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getAccessToken())
            .when()
            .put("{username}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString();


    TokenDTO tokenDTO = xmlMapper.readValue(content, TokenDTO.class);
    token =  tokenDTO;

    assertNotNull(token.getAccessToken());
    assertNotNull(token.getRefreshToken());

    assertEquals("leandro", tokenDTO.getUsername());
    assertTrue(tokenDTO.getAuthenticated());
  }
}