package BibliotecaAPI_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import BibliotecaAPI_spring.model.Livro;
import jakarta.validation.constraints.NotBlank;

public interface LivroRepository extends JpaRepository<Livro, Long>{
Page<Livro> findByAutorContainingIgnoreCase(String autor, Pageable pageable);
boolean existsByIsbn(String isbn);
boolean existsByIsbnAndIdNot(String isbn, Long id);
}
