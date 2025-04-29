package br.com.thiagodotjpeg.integrationstest.dto.wrapper.xml;

import br.com.thiagodotjpeg.integrationstest.dto.BookDTO;
import br.com.thiagodotjpeg.integrationstest.dto.PersonDTO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@XmlRootElement
public class PagedModelBook implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @XmlElement(name = "content")
  public List<BookDTO> content;

  public PagedModelBook() {
  }

  public List<BookDTO> getContent() {
    return content;
  }

  public void setContent(List<BookDTO> content) {
    this.content = content;
  }
}
