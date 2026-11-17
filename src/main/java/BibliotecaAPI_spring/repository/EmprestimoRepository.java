package BibliotecaAPI_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import BibliotecaAPI_spring.model.Emprestimo;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long>{

}
