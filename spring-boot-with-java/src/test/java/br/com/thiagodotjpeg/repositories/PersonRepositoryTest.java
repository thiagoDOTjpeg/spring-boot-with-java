package br.com.thiagodotjpeg.repositories;

import br.com.thiagodotjpeg.integrationstest.testcontainers.AbstractIntegrationTest;
import br.com.thiagodotjpeg.models.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "server.port=8888")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest {

  @Autowired
  PersonRepository repository;
  private static Person person;

  @BeforeAll
  static void setUp() {
    person = new Person();
  }

  @Test
  @Order(1)
  void findPeopleByName() {
    Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.ASC, "firstName"));

    person = repository.findPeopleByName("ayr", pageable).getContent().get(0);
    System.out.println(person);

    assertNotNull(person);
    assertNotNull(person.getId());
    assertEquals("Ayrton", person.getFirstName());
    assertEquals("Senna", person.getLastName());
    assertEquals("Male", person.getGender());
    assertTrue(person.getEnabled());
  }

  @Test
  @Order(2)
  void disablePerson() {
    long id = person.getId();
    repository.disablePerson(id);

    var result = repository.findById(id);
    person = result.get();


    assertNotNull(person);
    assertNotNull(person.getId());
    assertEquals("Ayrton", person.getFirstName());
    assertEquals("Senna", person.getLastName());
    assertEquals("Male", person.getGender());
    assertFalse(person.getEnabled());
  }

}