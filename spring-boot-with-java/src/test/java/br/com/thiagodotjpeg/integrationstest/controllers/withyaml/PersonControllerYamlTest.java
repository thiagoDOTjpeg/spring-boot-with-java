package br.com.thiagodotjpeg.integrationstest.controllers.withyaml;

import br.com.thiagodotjpeg.config.TestConfigs;
import br.com.thiagodotjpeg.integrationstest.controllers.withyaml.mapper.YAMLMapper;
import br.com.thiagodotjpeg.integrationstest.dto.AccountCredentialsDTO;
import br.com.thiagodotjpeg.integrationstest.dto.PersonDTO;
import br.com.thiagodotjpeg.integrationstest.dto.TokenDTO;
import br.com.thiagodotjpeg.integrationstest.dto.wrapper.xml.PagedModelPerson;
import br.com.thiagodotjpeg.integrationstest.testcontainers.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8888")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYamlTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static YAMLMapper yamlMapper;
  private static PersonDTO person;
  private static TokenDTO token;

  @BeforeAll
  static void setUp() {
    yamlMapper = new YAMLMapper();
    person = new PersonDTO();
    token = new TokenDTO();
  }

  @Test
  @Order(0)
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
  @Order(1)
  void create() throws IOException {
    mockPerson();

    specification = new RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_GRITTI)
            .addHeader(TestConfigs.HEADER_ACCEPT, MediaType.APPLICATION_YAML_VALUE)
            .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + token.getAccessToken())
            .setBasePath("/api/v1/person")
            .setPort(TestConfigs.SERVER_PORT)
              .addFilter(new RequestLoggingFilter(LogDetail.ALL))
              .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    var createdPerson = given(specification).config(
            RestAssuredConfig.config().encoderConfig(
                    EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
            .contentType(MediaType.APPLICATION_YAML_VALUE)
              .body(person, yamlMapper)
            .when()
              .post()
            .then()
              .statusCode(200)
            .extract()
              .body()
            .as(PersonDTO.class, yamlMapper);

    person = createdPerson;

    assertNotNull(createdPerson.getId());
    assertTrue(createdPerson.getId() > 0);

    assertEquals("Linus", createdPerson.getFirstName());
    assertEquals("Torvalds", createdPerson.getLastName());
    assertEquals("Helsinki - Finland", createdPerson.getAddress());
    assertEquals("Male", createdPerson.getGender());
    assertTrue(createdPerson.getEnabled());

  }

  @Test
  @Order(2)
  void updateTest() throws IOException {
    person.setLastName("Benedict Torvalds");

    var updatedPerson = given(specification).config(
                    RestAssuredConfig.config().encoderConfig(
                            EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .body(person, yamlMapper)
            .when()
            .put()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(PersonDTO.class, yamlMapper);

    person = updatedPerson;

    assertNotNull(updatedPerson.getId());
    assertTrue(updatedPerson.getId() > 0);

    assertEquals("Linus", updatedPerson.getFirstName());
    assertEquals("Benedict Torvalds", updatedPerson.getLastName());
    assertEquals("Helsinki - Finland", updatedPerson.getAddress());
    assertEquals("Male", updatedPerson.getGender());
    assertTrue(updatedPerson.getEnabled());
  }

  @Test
  @Order(3)
  void findById() throws IOException {


    var createdPerson = given(specification).config(
                    RestAssuredConfig.config().encoderConfig(
                            EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .pathParam("id", person.getId())
            .when()
            .get("{id}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(PersonDTO.class, yamlMapper);

    person = createdPerson;

    assertNotNull(createdPerson.getId());
    assertTrue(createdPerson.getId() > 0);

    assertEquals("Linus", createdPerson.getFirstName());
    assertEquals("Benedict Torvalds", createdPerson.getLastName());
    assertEquals("Helsinki - Finland", createdPerson.getAddress());
    assertEquals("Male", createdPerson.getGender());
    assertTrue(createdPerson.getEnabled());
  }

  @Test
  @Order(4)
  void disableTest() throws IOException {
    var updatedPerson = given(specification).config(
                    RestAssuredConfig.config().encoderConfig(
                            EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .pathParam("id", person.getId())
            .when()
            .patch("{id}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(PersonDTO.class, yamlMapper);

    person = updatedPerson;

    assertNotNull(updatedPerson.getId());
    assertTrue(updatedPerson.getId() > 0);

    assertEquals("Linus", updatedPerson.getFirstName());
    assertEquals("Benedict Torvalds", updatedPerson.getLastName());
    assertEquals("Helsinki - Finland", updatedPerson.getAddress());
    assertEquals("Male", updatedPerson.getGender());
    assertFalse(updatedPerson.getEnabled());
  }

  @Test
  @Order(5)
  void deleteTest() throws IOException {
    given(specification)
              .pathParam("id", person.getId()) 
            .when()
              .delete("{id}")
            .then()
              .statusCode(204);
  }

  @Test
  @Order(6)
  void findAll() throws IOException {
    var content = given(specification).config(
                    RestAssuredConfig.config().encoderConfig(
                            EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
            .accept(MediaType.APPLICATION_YAML_VALUE)
            .queryParams("page", 3, "size", 12, "direction", "asc")
            .when()
            .get()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .extract()
            .body()
            .as(PagedModelPerson.class, yamlMapper);

    List<PersonDTO> people = content.getContent();

    PersonDTO personOne = people.get(0);
    person = personOne;

    assertNotNull(personOne.getId());
    assertTrue(personOne.getId() > 0);

    assertEquals("Angie", personOne.getFirstName());
    assertEquals("Callery", personOne.getLastName());
    assertEquals("Apt 808", personOne.getAddress());
    assertEquals("Female", personOne.getGender());
    assertFalse(personOne.getEnabled());

    PersonDTO personTwo = people.get(1);
    person = personTwo;

    assertNotNull(personTwo.getId());
    assertTrue(personTwo.getId() > 0);

    assertEquals("Anna", personTwo.getFirstName());
    assertEquals("Lorenzetti", personTwo.getLastName());
    assertEquals("Suite 59", personTwo.getAddress());
    assertEquals("Female", personTwo.getGender());
    assertFalse(personTwo.getEnabled());

    PersonDTO personThree = people.get(2);
    person = personThree;

    assertNotNull(personThree.getId());
    assertTrue(personThree.getId() > 0);

    assertEquals("Annemarie", personThree.getFirstName());
    assertEquals("Seeds", personThree.getLastName());
    assertEquals("Suite 95", personThree.getAddress());
    assertEquals("Female", personThree.getGender());
    assertFalse(personThree.getEnabled());
  }

  @Test
  @Order(7)
  void findByNameTest() throws IOException {
    var content = given(specification)
            .accept(MediaType.APPLICATION_YAML_VALUE)
            .pathParam("firstName", "an")
            .queryParams("page", 3, "size", 12, "direction", "asc")
            .when()
            .get("findPeopleByName/{firstName}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(PagedModelPerson.class, yamlMapper);

    List<PersonDTO> people = content.getContent();

    PersonDTO personOne = people.get(0);
    person = personOne;

    assertNotNull(personOne.getId());
    assertTrue(personOne.getId() > 0);

    assertEquals("Daniele", personOne.getFirstName());
    assertEquals("Hardwin", personOne.getLastName());
    assertEquals("Room 1574", personOne.getAddress());
    assertEquals("Female", personOne.getGender());
    assertTrue(personOne.getEnabled());

    PersonDTO personTwo = people.get(1);
    person = personTwo;

    assertNotNull(personTwo.getId());
    assertTrue(personTwo.getId() > 0);

    assertEquals("Dannie", personTwo.getFirstName());
    assertEquals("Sheere", personTwo.getLastName());
    assertEquals("Room 138", personTwo.getAddress());
    assertEquals("Male", personTwo.getGender());
    assertFalse(personTwo.getEnabled());

    PersonDTO personThree = people.get(2);
    person = personThree;

    assertNotNull(personThree.getId());
    assertTrue(personThree.getId() > 0);

    assertEquals("Deeanne", personThree.getFirstName());
    assertEquals("MacCart", personThree.getLastName());
    assertEquals("Apt 157", personThree.getAddress());
    assertEquals("Female", personThree.getGender());
    assertTrue(personThree.getEnabled());
  }

  private void mockPerson() {
    person.setFirstName("Linus");
    person.setLastName("Torvalds");
    person.setAddress("Helsinki - Finland");
    person.setGender("Male");
    person.setEnabled(true);
  }


}