package BibliotecaAPI_spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import BibliotecaAPI_spring.model.Emprestimo;
import BibliotecaAPI_spring.model.Status;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long>{
List<Emprestimo> findByStatus(Status status);
long countByUsuarioIdAndStatus(Long id, Status status);
}
