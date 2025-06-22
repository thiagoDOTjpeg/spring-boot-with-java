package br.com.thiagodotjpeg.file.importer.contract;

import br.com.thiagodotjpeg.data.dto.v1.PersonDTO;

import java.io.InputStream;
import java.util.List;

public interface FileImporter {
  List<PersonDTO> importFile(InputStream inputStream) throws Exception;
}
