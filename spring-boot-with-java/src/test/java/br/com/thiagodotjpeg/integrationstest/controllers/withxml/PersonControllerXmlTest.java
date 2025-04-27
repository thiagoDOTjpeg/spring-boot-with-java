package br.com.thiagodotjpeg.integrationstest.controllers.withxml;

import br.com.thiagodotjpeg.config.TestConfigs;
import br.com.thiagodotjpeg.integrationstest.dto.PersonDTO;
import br.com.thiagodotjpeg.integrationstest.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8888")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerXmlTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static ObjectMapper objectMapper;
  private static XmlMapper xmlMapper;
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
            .addHeader(TestConfigs.HEADER_ACCEPT, MediaType.APPLICATION_XML_VALUE)
            .setBasePath("/api/v1/person")
            .setPort(TestConfigs.SERVER_PORT)
              .addFilter(new RequestLoggingFilter(LogDetail.ALL))
              .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    String personXml = xmlMapper.writeValueAsString(person);

    var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
              .body(person)
            .when()
              .post()
            .then()
              .statusCode(200)
            .extract()
              .body()
            .asString();

    PersonDTO createdPerson = xmlMapper.readValue(content, PersonDTO.class);
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

    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    String personXml = xmlMapper.writeValueAsString(person);


    var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .body(person)
            .when()
            .put()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString();

    PersonDTO updatedPerson = xmlMapper.readValue(content, PersonDTO.class);
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
    specification = new RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_GRITTI)
            .addHeader(TestConfigs.HEADER_ACCEPT, MediaType.APPLICATION_XML_VALUE)
            .setBasePath("/api/v1/person")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    String personXml = xmlMapper.writeValueAsString(person);

    var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .pathParam("id", person.getId())
            .when()
            .get("{id}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString();

    PersonDTO createdPerson = xmlMapper.readValue(content, PersonDTO.class);
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
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    String personXml = xmlMapper.writeValueAsString(person);

    var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .pathParam("id", person.getId())
            .when()
            .patch("{id}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString();

    PersonDTO updatedPerson = xmlMapper.readValue(content, PersonDTO.class);
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
    specification = new RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_GRITTI)
            .addHeader(TestConfigs.HEADER_ACCEPT, MediaType.APPLICATION_XML_VALUE)
            .setBasePath("/api/v1/person")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    String personXml = xmlMapper.writeValueAsString(person);

    var content = given(specification)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .when()
            .get()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

    JavaType personType = xmlMapper.getTypeFactory().constructParametricType(List.class, PersonDTO.class);
    List<PersonDTO> people = xmlMapper.readValue(content, personType);

    PersonDTO personOne = people.get(0);
    person = personOne;

    assertNotNull(personOne.getId());
    assertTrue(personOne.getId() > 0);

    assertEquals("Thiago", personOne.getFirstName());
    assertEquals("Gritti", personOne.getLastName());
    assertEquals("Balneário Camboriú - Santa Catarina - Brasil", personOne.getAddress());
    assertEquals("Male", personOne.getGender());
    assertTrue(personOne.getEnabled());

    PersonDTO personTwo = people.get(1);
    person = personTwo;

    assertNotNull(personTwo.getId());
    assertTrue(personTwo.getId() > 0);

    assertEquals("Ayrton", personTwo.getFirstName());
    assertEquals("Senna", personTwo.getLastName());
    assertEquals("Curitiba", personTwo.getAddress());
    assertEquals("Male", personTwo.getGender());
    assertTrue(personTwo.getEnabled());

    PersonDTO personThree = people.get(2);
    person = personThree;

    assertNotNull(personThree.getId());
    assertTrue(personThree.getId() > 0);

    assertEquals("Vendra", personThree.getFirstName());
    assertEquals("Brigada", personThree.getLastName());
    assertEquals("São Paulo", personThree.getAddress());
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