package br.com.thiagodotjpeg.integrationstest.dto.wrapper.json;

import br.com.thiagodotjpeg.integrationstest.dto.PersonDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PersonEmbededdDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @JsonProperty("people")
  private List<PersonDTO> people;

  public PersonEmbededdDTO() {
  }

  public List<PersonDTO> getPeople() {
    return people;
  }

  public void setPeople(List<PersonDTO> people) {
    this.people = people;
  }
}
