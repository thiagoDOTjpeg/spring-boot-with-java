package br.com.thiagodotjpeg.integrationstest.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@XmlRootElement
public class BookDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  @JacksonXmlProperty(localName = "id")
  private Long id;

  @JacksonXmlProperty(localName = "author")
  private String author;

  @JacksonXmlProperty(localName = "title")
  private String title;

  @JacksonXmlProperty(localName = "launchDate")
  private Date launchDate;

  @JacksonXmlProperty(localName = "price")
  private Double price;

  public BookDTO() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getLaunchDate() {
    return launchDate;
  }

  public void setLaunchDate(Date launchDate) {
    this.launchDate = launchDate;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    BookDTO bookDTO = (BookDTO) o;
    return Objects.equals(id, bookDTO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id);
  }
}
