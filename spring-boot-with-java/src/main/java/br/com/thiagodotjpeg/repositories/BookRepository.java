package br.com.thiagodotjpeg.repositories;

import br.com.thiagodotjpeg.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
