package br.com.thiagodotjpeg.integrationstest.controllers.withyaml;

import br.com.thiagodotjpeg.config.TestConfigs;
import br.com.thiagodotjpeg.integrationstest.controllers.withyaml.mapper.YAMLMapper;
import br.com.thiagodotjpeg.integrationstest.dto.BookDTO;
import br.com.thiagodotjpeg.integrationstest.dto.PersonDTO;
import br.com.thiagodotjpeg.integrationstest.dto.wrapper.xml.PagedModelBook;
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
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = "server.port=8888")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerYamlTest extends AbstractIntegrationTest {

  private static RequestSpecification specification;
  private static YAMLMapper yamlMapper;
  private static BookDTO book;

  @BeforeAll
  static void setUp() {
    yamlMapper = new YAMLMapper();
    book = new BookDTO();
  }

  @Test
  @Order(1)
  void create() throws IOException {
    mockPerson();

    specification = new RequestSpecBuilder()
            .addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_GRITTI)
            .addHeader(TestConfigs.HEADER_ACCEPT, MediaType.APPLICATION_YAML_VALUE)
            .setBasePath("/api/v1/book")
            .setPort(TestConfigs.SERVER_PORT)
              .addFilter(new RequestLoggingFilter(LogDetail.ALL))
              .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

    var createdBook = given(specification).config(
            RestAssuredConfig.config().encoderConfig(
                    EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
            .contentType(MediaType.APPLICATION_YAML_VALUE)
              .body(book, yamlMapper)
            .when()
              .post()
            .then()
              .statusCode(200)
            .extract()
              .body()
            .as(BookDTO.class, yamlMapper);

    book = createdBook;

    assertNotNull(createdBook.getId());
    assertTrue(createdBook.getId() > 0);

    assertEquals("Michael C. Feathers", createdBook.getAuthor());
    assertEquals("Working effectively with legacy code", createdBook.getTitle());
    assertEquals(49.00, createdBook.getPrice());
    assertInstanceOf(Date.class, createdBook.getLaunchDate());
  }

  @Test
  @Order(2)
  void updateTest() throws IOException {
    book.setAuthor("Robert C. Martin");

    var updatedBook = given(specification).config(
                    RestAssuredConfig.config().encoderConfig(
                            EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .body(book, yamlMapper)
            .when()
            .patch()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(BookDTO.class, yamlMapper);

    book = updatedBook;

    assertNotNull(updatedBook.getId());
    assertTrue(updatedBook.getId() > 0);

    assertEquals("Robert C. Martin", updatedBook.getAuthor());
    assertEquals("Working effectively with legacy code", updatedBook.getTitle());
    assertEquals(49.00, updatedBook.getPrice());
    assertInstanceOf(Date.class, updatedBook.getLaunchDate());
  }

  @Test
  @Order(3)
  void findById() throws IOException {
    var createdBook = given(specification).config(
                    RestAssuredConfig.config().encoderConfig(
                            EncoderConfig.encoderConfig().encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
            .contentType(MediaType.APPLICATION_YAML_VALUE)
            .pathParam("id", book.getId())
            .when()
            .get("{id}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .as(BookDTO.class, yamlMapper);

    book = createdBook;

    assertNotNull(createdBook.getId());
    assertTrue(createdBook.getId() > 0);

    assertEquals("Robert C. Martin", createdBook.getAuthor());
    assertEquals("Working effectively with legacy code", createdBook.getTitle());
    assertEquals(49.00, createdBook.getPrice());
    assertInstanceOf(Date.class, createdBook.getLaunchDate());
  }

  @Test
  @Order(5)
  void deleteTest() throws IOException {
    given(specification)
              .pathParam("id", book.getId())
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
            .as(PagedModelBook.class, yamlMapper);

    List<BookDTO> books = content.getContent();

    BookDTO bookOne = books.get(0);
    book = bookOne;

    assertNotNull(bookOne.getId());
    assertTrue(bookOne.getId() > 0);

    assertEquals("Courtney Hayes", bookOne.getAuthor());
    assertEquals("Desert Mirage", bookOne.getTitle());
    assertInstanceOf(Date.class, bookOne.getLaunchDate());
    assertEquals(90.77, bookOne.getPrice());

    BookDTO bookTwo = books.get(1);
    book = bookTwo;

    assertNotNull(bookTwo.getId());
    assertTrue(bookTwo.getId() > 0);

    assertEquals("Emily Johnson", bookTwo.getAuthor());
    assertEquals("Desert Mirage", bookTwo.getTitle());
    assertInstanceOf(Date.class, bookTwo.getLaunchDate());
    assertEquals(14.8, bookTwo.getPrice());

    BookDTO bookThree = books.get(2);
    book = bookThree;

    assertNotNull(bookThree.getId());
    assertTrue(bookThree.getId() > 0);

    assertEquals("Ralph Johnson, Erich Gamma, John Vlissides e Richard Helm", bookThree.getAuthor());
    assertEquals("Design Patterns", bookThree.getTitle());
    assertInstanceOf(Date.class, bookThree.getLaunchDate());
    assertEquals(45.0, bookThree.getPrice());
  }

  private void mockPerson() {
    book.setAuthor("Michael C. Feathers");
    book.setTitle("Working effectively with legacy code");
    book.setPrice(49.00);
    book.setLaunchDate(new Date());
  }


}