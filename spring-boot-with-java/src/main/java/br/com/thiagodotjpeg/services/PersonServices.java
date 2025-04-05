package br.com.thiagodotjpeg.services;

import br.com.thiagodotjpeg.controllers.PersonController;
import br.com.thiagodotjpeg.data.dto.v1.PersonDTO;
import br.com.thiagodotjpeg.exceptions.RequiredObjectIsNullException;
import br.com.thiagodotjpeg.exceptions.ResourceNotFoundException;
import br.com.thiagodotjpeg.mapper.custom.PersonMapper;
import br.com.thiagodotjpeg.models.Person;
import br.com.thiagodotjpeg.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.thiagodotjpeg.mapper.ObjectMapper.parselistobjects;
import static br.com.thiagodotjpeg.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
public class PersonServices {

    @Autowired
    private PersonRepository repository;

    @Autowired
    private PersonMapper converter;

    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());

    public List<PersonDTO> findAll() {
        logger.info("Finding all people");
        var dto = parselistobjects(repository.findAll(), PersonDTO.class);
        dto.forEach(PersonServices::addHateoasLinks);
        return dto;
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one person");
        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        var dto = parseObject(entity, PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public PersonDTO create(PersonDTO person) {
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Creating a new person");
        Person entity = parseObject(person, Person.class);
        var dto = parseObject(repository.save(entity), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public PersonDTO update(PersonDTO person) {
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Updating a new person");
        Person updatedPerson = repository.findById(person.getId()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        updatedPerson.setFirstName(person.getFirstName());
        updatedPerson.setLastName(person.getLastName());
        updatedPerson.setAddress(person.getAddress());
        updatedPerson.setGender(person.getGender());
        repository.save(updatedPerson);
        var dto = parseObject(updatedPerson, PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting a person by id");
        Person person = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        repository.delete(person);
    }

    private static void addHateoasLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto)).withRel("update").withType("PATCH"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }

}
