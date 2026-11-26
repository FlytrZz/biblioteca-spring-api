package BibliotecaAPI_spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import BibliotecaAPI_spring.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
boolean existsByEmail(String email);
boolean existsByEmailAndIdNot(String email, Long id);
}
