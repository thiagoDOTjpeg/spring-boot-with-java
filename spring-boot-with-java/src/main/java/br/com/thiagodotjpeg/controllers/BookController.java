package br.com.thiagodotjpeg.controllers;

import br.com.thiagodotjpeg.controllers.docs.BookControllerDocs;
import br.com.thiagodotjpeg.data.dto.v1.BookDTO;
import br.com.thiagodotjpeg.services.BookServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/book")
@Tag(name = "Book", description = "Endpoints for Managing Book")
public class BookController implements BookControllerDocs {
  @Autowired
  private BookServices services;

  public BookController(BookServices services) {
    this.services = services;
  }

  @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE,
                          MediaType.APPLICATION_XML_VALUE,
                          MediaType.APPLICATION_YAML_VALUE})
  @Override
  public List<BookDTO> findAll() {
    return services.findAll();
  }

  @GetMapping(value = "/{id}",
          consumes = {MediaType.APPLICATION_JSON_VALUE,
                      MediaType.APPLICATION_XML_VALUE,
                      MediaType.APPLICATION_YAML_VALUE},
          produces = {MediaType.APPLICATION_JSON_VALUE,
                      MediaType.APPLICATION_XML_VALUE,
                      MediaType.APPLICATION_YAML_VALUE})
  @Override
  public BookDTO findById(@PathVariable(name = "id") Long id) {
    return services.findById(id);
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
                            MediaType.APPLICATION_XML_VALUE,
                            MediaType.APPLICATION_YAML_VALUE},
              produces = {MediaType.APPLICATION_JSON_VALUE,
                          MediaType.APPLICATION_XML_VALUE,
                          MediaType.APPLICATION_YAML_VALUE})
  @Override
  public BookDTO create(@RequestBody BookDTO person) {
    return services.create(person);
  }

  @PatchMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
                            MediaType.APPLICATION_XML_VALUE,
                            MediaType.APPLICATION_YAML_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE,
                            MediaType.APPLICATION_XML_VALUE,
                            MediaType.APPLICATION_YAML_VALUE})
  @Override
  public BookDTO update(@RequestBody BookDTO book) {
    return services.update(book);
  }

  @DeleteMapping(value = "/{id}")
  @Override
  public ResponseEntity<?> delete(@PathVariable(name = "id") Long id) {
    services.delete(id);
    return ResponseEntity.noContent().build();
  }
}
