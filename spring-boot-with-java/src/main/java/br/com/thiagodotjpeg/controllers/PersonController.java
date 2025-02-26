package br.com.thiagodotjpeg.controllers;

import br.com.thiagodotjpeg.data.dto.v2.PersonDTOV2;
import br.com.thiagodotjpeg.services.PersonServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("api/v1/person")
public class PersonController {

    @Autowired
    private PersonServices service;

    @GetMapping(value = "/{id}" ,produces = MediaType.APPLICATION_JSON_VALUE)
    public br.com.thiagodotjpeg.data.dto.v1.PersonDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<br.com.thiagodotjpeg.data.dto.v1.PersonDTO> findALl() {
        return service.findALl();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public br.com.thiagodotjpeg.data.dto.v1.PersonDTO create(@RequestBody br.com.thiagodotjpeg.data.dto.v1.PersonDTO person) {
        return service.create(person);
    }

    @PostMapping(value = "/v2", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PersonDTOV2 create(@RequestBody PersonDTOV2 person) {
        return service.createV2(person);
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public br.com.thiagodotjpeg.data.dto.v1.PersonDTO update(@RequestBody br.com.thiagodotjpeg.data.dto.v1.PersonDTO person) {
        return service.update(person);
    }

    @DeleteMapping(value = "/{id}" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
