package br.com.thiagodotjpeg.integrationstest.dto.wrapper.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class WrapperBookDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @JsonProperty("_embedded")
  private BookEmbededdDTO embedded;

  public WrapperBookDTO() {
  }

  public BookEmbededdDTO getEmbedded() {
    return embedded;
  }

  public void setEmbedded(BookEmbededdDTO embedded) {
    this.embedded = embedded;
  }
}
