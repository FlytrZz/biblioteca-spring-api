package BibliotecaAPI_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import BibliotecaAPI_spring.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

}
