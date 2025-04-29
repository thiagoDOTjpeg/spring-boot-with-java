package br.com.thiagodotjpeg.integrationstest.dto.wrapper.json;

import br.com.thiagodotjpeg.integrationstest.dto.BookDTO;
import br.com.thiagodotjpeg.integrationstest.dto.PersonDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class BookEmbededdDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @JsonProperty("books")
  private List<BookDTO> books;

  public BookEmbededdDTO() {
  }

  public List<BookDTO> getBooks() {
    return books;
  }

  public void setBooks(List<BookDTO> books) {
    this.books = books;
  }
}
