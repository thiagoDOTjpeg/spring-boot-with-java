package br.com.thiagodotjpeg.integrationstest.controllers.withxml;

import br.com.thiagodotjpeg.config.TestConfigs;
import br.com.thiagodotjpeg.integrationstest.dto.PersonDTO;
import br.com.thiagodotjpeg.integrationstest.dto.wrapper.json.WrapperPersonDTO;
import br.com.thiagodotjpeg.integrationstest.dto.wrapper.xml.PagedModelPerson;
import br.com.thiagodotjpeg.integrationstest.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
  private static XmlMapper xmlMapper;
  private static PersonDTO person;

  @BeforeAll
  static void setUp() {
    xmlMapper = new XmlMapper();
    xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

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
    var content = given(specification)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .queryParams("page", 3, "size", 12, "direction", "asc")
            .when()
            .get()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

    PagedModelPerson wrapper = xmlMapper.readValue(content, PagedModelPerson.class);
    List<PersonDTO> people = wrapper.getContent();

    PersonDTO personOne = people.get(0);
    person = personOne;

    assertNotNull(personOne.getId());
    assertTrue(personOne.getId() > 0);

    assertEquals("Anna", personOne.getFirstName());
    assertEquals("Lorenzetti", personOne.getLastName());
    assertEquals("Suite 59", personOne.getAddress());
    assertEquals("Female", personOne.getGender());
    assertFalse(personOne.getEnabled());

    PersonDTO personTwo = people.get(1);
    person = personTwo;

    assertNotNull(personTwo.getId());
    assertTrue(personTwo.getId() > 0);

    assertEquals("Annemarie", personTwo.getFirstName());
    assertEquals("Seeds", personTwo.getLastName());
    assertEquals("Suite 95", personTwo.getAddress());
    assertEquals("Female", personTwo.getGender());
    assertFalse(personTwo.getEnabled());

    PersonDTO personThree = people.get(2);
    person = personThree;

    assertNotNull(personThree.getId());
    assertTrue(personThree.getId() > 0);

    assertEquals("Anthia", personThree.getFirstName());
    assertEquals("Piggin", personThree.getLastName());
    assertEquals("8th Floor", personThree.getAddress());
    assertEquals("Female", personThree.getGender());
    assertFalse(personThree.getEnabled());
  }

  @Test
  @Order(7)
  void findByNameTest() throws IOException {
    var content = given(specification)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .pathParam("firstName", "an")
            .queryParams("page", 3, "size", 12, "direction", "asc")
            .when()
            .get("findPeopleByName/{firstName}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString();

    PagedModelPerson wrapper = xmlMapper.readValue(content, PagedModelPerson.class);
    List<PersonDTO> people = wrapper.getContent();

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