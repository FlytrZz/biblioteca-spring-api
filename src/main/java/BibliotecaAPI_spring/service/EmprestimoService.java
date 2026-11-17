package BibliotecaAPI_spring.service;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import BibliotecaAPI_spring.DTO.EmprestimoDTO;
import BibliotecaAPI_spring.model.Emprestimo;
import BibliotecaAPI_spring.repository.EmprestimoRepository;

public class EmprestimoService {

	@Autowired
	private EmprestimoRepository repository;
	
	public List<Emprestimo> listar() {
		return repository.findAll();
	}

	public Emprestimo buscarID(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public Emprestimo criarEmprestimo() {
		// TODO Auto-generated method stub
		return null;
	}
}
