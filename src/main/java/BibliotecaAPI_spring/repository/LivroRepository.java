package BibliotecaAPI_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import BibliotecaAPI_spring.model.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long>{
Page<Livro> findByAutorContainingIgnoreCase(String autor, Pageable pageable);
}
