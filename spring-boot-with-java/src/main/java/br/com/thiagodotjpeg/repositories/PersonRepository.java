package br.com.thiagodotjpeg.repositories;

import br.com.thiagodotjpeg.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {}
