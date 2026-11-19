package BibliotecaAPI_spring.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import BibliotecaAPI_spring.DTO.LivroDTO;
import BibliotecaAPI_spring.model.Livro;
import BibliotecaAPI_spring.service.LivroService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/livros")
public class LivroController {

	@Autowired
	private LivroService service;
	
	@GetMapping
	public List<Livro> listar() {
		return service.listarLivros();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Livro> buscarPorID(@PathVariable("id") Long id) {
		return ResponseEntity.ok(service.buscarID(id));
	}
	
	@PostMapping
	public ResponseEntity<Livro> cadastrar(@RequestBody @Valid LivroDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.criarLivro(dto));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Livro> atualizar(@PathVariable("id") Long id, @RequestBody @Valid LivroDTO dto) {
		return ResponseEntity.ok(service.atualizarLivro(id, dto));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Livro> deletar(@PathVariable("id") Long id) {
		service.apagarLivro(id);
		return ResponseEntity.noContent().build();
	}
	
}
