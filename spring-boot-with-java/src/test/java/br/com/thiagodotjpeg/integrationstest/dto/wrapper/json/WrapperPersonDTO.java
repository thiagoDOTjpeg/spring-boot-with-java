package br.com.thiagodotjpeg.integrationstest.dto.wrapper.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class WrapperPersonDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @JsonProperty("_embedded")
  private PersonEmbededdDTO embedded;

  public WrapperPersonDTO() {
  }

  public PersonEmbededdDTO getEmbedded() {
    return embedded;
  }

  public void setEmbedded(PersonEmbededdDTO embedded) {
    this.embedded = embedded;
  }
}
