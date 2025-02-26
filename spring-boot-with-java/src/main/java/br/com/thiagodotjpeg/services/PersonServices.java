package br.com.thiagodotjpeg.services;

import br.com.thiagodotjpeg.data.dto.v1.PersonDTO;
import br.com.thiagodotjpeg.data.dto.v2.PersonDTOV2;
import br.com.thiagodotjpeg.exceptions.ResourceNotFoundException;
import br.com.thiagodotjpeg.mapper.custom.PersonMapper;
import br.com.thiagodotjpeg.models.Person;
import br.com.thiagodotjpeg.repositories.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static br.com.thiagodotjpeg.mapper.ObjectMapper.parselistobjects;
import static br.com.thiagodotjpeg.mapper.ObjectMapper.parseObject;

@Service
public class PersonServices {

    @Autowired
    private PersonRepository repository;

    @Autowired
    private PersonMapper converter;

    private Logger logger = LoggerFactory.getLogger(PersonServices.class.getName());
    private final AtomicLong counter = new AtomicLong();

    public List<PersonDTO> findALl() {
        logger.info("Finding all people");
        return parselistobjects(repository.findAll(), PersonDTO.class);
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one person");
        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        return parseObject(entity, PersonDTO.class);
    }

    public br.com.thiagodotjpeg.data.dto.v1.PersonDTO create(PersonDTO person) {
        logger.info("Creating a new person");
        Person entity = parseObject(person, Person.class);
        return parseObject(repository.save(entity), PersonDTO.class);
    }

    public PersonDTOV2 createV2(PersonDTOV2 person) {
        logger.info("Creating a new person V2");
        Person entity = converter.convertDTOtoEntity(person);
        return converter.convertEntityToDTO(repository.save(entity));
    }

    public PersonDTO update(PersonDTO person) {
        logger.info("Updating a new person");
        Person updatedPerson = repository.findById(person.getId()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        updatedPerson.setFirstName(person.getFirstName());
        updatedPerson.setLastName(person.getLastName());
        updatedPerson.setAddress(person.getAddress());
        updatedPerson.setGender(person.getGender());
        repository.save(updatedPerson);
        return parseObject(updatedPerson, PersonDTO.class);
    }

    public void delete(Long id) {
        logger.info("Deleting a person by id");
        Person person = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
        repository.delete(person);
    }

}
