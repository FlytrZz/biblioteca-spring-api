package BibliotecaAPI_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import BibliotecaAPI_spring.DTO.EmprestimoDTO;
import BibliotecaAPI_spring.model.Emprestimo;
import BibliotecaAPI_spring.model.Status;
import BibliotecaAPI_spring.service.EmprestimoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/emprestimos")
public class EmprestimoController {

	@Autowired
	private EmprestimoService service;
	
	@GetMapping
	public PagedModel<Emprestimo> listar(@RequestParam(required = false) Status status, @PageableDefault(size = 5) Pageable pageable) {
		return new PagedModel<>(service.listarEmprestimos(status, pageable));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Emprestimo> buscarPorID(@PathVariable("id") Long id) {
		return ResponseEntity.ok(service.buscarID(id));
	}
	
	@PostMapping
	public ResponseEntity<Emprestimo> cadastrar(@RequestBody @Valid EmprestimoDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.criarEmprestimo(dto));
	}
	
	@PatchMapping("/{id}/devolucao")
	public ResponseEntity<Emprestimo> devolver(@PathVariable("id") Long id) {
		return ResponseEntity.ok(service.devolverEmprestimo(id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Emprestimo> deletar(@PathVariable("id") Long id) {
		service.apagarEmprestimo(id);
		return ResponseEntity.noContent().build();
	}
}
