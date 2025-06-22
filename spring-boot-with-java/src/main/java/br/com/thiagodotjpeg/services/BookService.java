package br.com.thiagodotjpeg.services;

import br.com.thiagodotjpeg.controllers.BookController;
import br.com.thiagodotjpeg.data.dto.v1.BookDTO;
import br.com.thiagodotjpeg.exceptions.RequiredObjectIsNullException;
import br.com.thiagodotjpeg.exceptions.ResourceNotFoundException;
import br.com.thiagodotjpeg.models.Book;
import br.com.thiagodotjpeg.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import static br.com.thiagodotjpeg.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {
  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private PagedResourcesAssembler<BookDTO> assembler;

  public PagedModel<EntityModel<BookDTO>> findAll(Pageable pageable) {
    Page<Book> books = bookRepository.findAll(pageable);
    Page<BookDTO> booksWithLinks = books.map(b -> {
      var dto = parseObject(b, BookDTO.class);
      addHateoasLinks(dto);
      return dto;
    });

    Link findAllLinks = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), String.valueOf(pageable.getSort()))).withSelfRel();

    return assembler.toModel(booksWithLinks, findAllLinks);
  }

  public BookDTO findById(Long id) {
    Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    BookDTO bookDTO = parseObject(book, BookDTO.class);
    addHateoasLinks(bookDTO);
    return bookDTO;
  }

  public BookDTO create(BookDTO data) {
    if(data == null) throw new RequiredObjectIsNullException();

    Book entity = parseObject(data, Book.class);
    BookDTO bookDTO = parseObject(bookRepository.save(entity), BookDTO.class);
    addHateoasLinks(bookDTO);
    return bookDTO;
  }

  public BookDTO update(BookDTO data) {
    if(data == null) throw new RequiredObjectIsNullException();

    Book updatedBook = bookRepository.findById(data.getId()).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    updatedBook.setTitle(data.getTitle());
    updatedBook.setAuthor(data.getAuthor());
    updatedBook.setLaunchDate(data.getLaunchDate());
    updatedBook.setPrice(data.getPrice());
    bookRepository.save(updatedBook);
    BookDTO bookDTO = parseObject(updatedBook, BookDTO.class);
    addHateoasLinks(bookDTO);
    return bookDTO;
  }

  public void delete(Long id) {
    Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    bookRepository.delete(book);
  }

  private static void addHateoasLinks(BookDTO dto) {
    dto.add(linkTo(methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
    dto.add(linkTo(methodOn(BookController.class).findAll(1, 12, "asc")).withRel("findAll").withType("GET"));
    dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("create").withType("POST"));
    dto.add(linkTo(methodOn(BookController.class).update(dto)).withRel("update").withType("PATCH"));
    dto.add(linkTo(methodOn(BookController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
  }
}
