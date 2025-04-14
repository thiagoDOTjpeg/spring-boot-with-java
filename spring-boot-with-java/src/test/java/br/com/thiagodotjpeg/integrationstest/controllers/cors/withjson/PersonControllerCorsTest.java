package br.com.thiagodotjpeg.integrationstest.controllers.cors.withjson;

import br.com.thiagodotjpeg.config.TestConfigs;
import br.com.thiagodotjpeg.integrationstest.dto.PersonDTO;
import br.com.thiagodotjpeg.integrationstest.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8888")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerCorsTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper objectMapper;
  private static PersonDTO person;

  @BeforeAll
  static void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    person = new PersonDTO();
  }

  @Test
  @Order(1)
  void create() throws IOException {
    mockPerson();

    specification = new RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_GRITTI)
            .setBasePath("/api/v1/person")
            .setPort(TestConfigs.SERVER_PORT)
              .addFilter(new RequestLoggingFilter(LogDetail.ALL))
              .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    var content = given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(person)
            .when()
              .post()
            .then()
              .statusCode(200)
            .extract()
              .body()
                .asString();

    PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
    person = createdPerson;

    assertNotNull(createdPerson.getId());
    assertNotNull(createdPerson.getFirstName());
    assertNotNull(createdPerson.getLastName());
    assertNotNull(createdPerson.getAddress());
    assertNotNull(createdPerson.getGender());

    assertTrue(createdPerson.getId() > 0);

    assertEquals("Richard", createdPerson.getFirstName());
    assertEquals("Stallman", createdPerson.getLastName());
    assertEquals("New York City - New York", createdPerson.getAddress());
    assertEquals("Male", createdPerson.getGender());
    assertTrue(createdPerson.getEnabled());
  }

  @Test
  @Order(2)
  void createWithWrongOrigin() throws IOException {
    mockPerson();

    specification = new RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_TEST)
            .setBasePath("/api/v1/person")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    var content = given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(person)
            .when()
            .post()
            .then()
            .statusCode(403)
            .extract()
            .body()
            .asString();

    assertEquals("Invalid CORS request", content);
  }

  @Test
  @Order(3)
  void findById() throws IOException {
    specification = new RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_GRITTI)
            .setBasePath("/api/v1/person")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    var content = given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", person.getId())
            .when()
            .get("{id}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString();

    PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
    person = createdPerson;

    assertNotNull(createdPerson.getId());
    assertNotNull(createdPerson.getFirstName());
    assertNotNull(createdPerson.getLastName());
    assertNotNull(createdPerson.getAddress());
    assertNotNull(createdPerson.getGender());

    assertTrue(createdPerson.getId() > 0);

    assertEquals("Richard", createdPerson.getFirstName());
    assertEquals("Stallman", createdPerson.getLastName());
    assertEquals("New York City - New York", createdPerson.getAddress());
    assertEquals("Male", createdPerson.getGender());
    assertTrue(createdPerson.getEnabled());
  }

  @Test
  @Order(4)
  void findByIdWithWrongOrigin() throws IOException {
    specification = new RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_TEST)
            .setBasePath("/api/v1/person")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    var content = given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", person.getId())
            .when()
            .get("{id}")
            .then()
            .statusCode(403)
            .extract()
            .body()
            .asString();

    assertEquals("Invalid CORS request", content);
  }

  private void mockPerson() {
    person.setFirstName("Richard");
    person.setLastName("Stallman");
    person.setAddress("New York City - New York");
    person.setGender("Male");
    person.setEnabled(true);
  }
}