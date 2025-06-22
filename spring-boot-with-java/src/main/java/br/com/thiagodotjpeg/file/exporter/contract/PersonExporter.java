package br.com.thiagodotjpeg.file.exporter.contract;

import br.com.thiagodotjpeg.data.dto.v1.PersonDTO;
import org.springframework.core.io.Resource;

import java.util.List;

public interface PersonExporter {
   Resource exportPeople(List<PersonDTO> people) throws Exception;
   Resource exportPerson(PersonDTO person) throws Exception;
}
