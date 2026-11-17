package BibliotecaAPI_spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import BibliotecaAPI_spring.service.EmprestimoService;
import jakarta.validation.Valid;
import BibliotecaAPI_spring.DTO.EmprestimoDTO;
import BibliotecaAPI_spring.model.Emprestimo;

@RestController
@RequestMapping("/v1/emprestimos")
public class EmprestimoController {

	@Autowired
	private EmprestimoService service;
	
	@GetMapping
	public List<Emprestimo> listar() {
		return service.listar();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Emprestimo> buscarPorID(@PathVariable("id") Long id) {
		return ResponseEntity.ok(service.buscarID(id));
	}
	
	@PostMapping
	public ResponseEntity<Emprestimo> cadastrar(@RequestBody @Valid EmprestimoDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.criarEmprestimo());
	}
}
