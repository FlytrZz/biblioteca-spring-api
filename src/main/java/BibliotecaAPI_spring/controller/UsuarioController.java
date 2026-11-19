package BibliotecaAPI_spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import BibliotecaAPI_spring.DTO.UsuarioDTO;
import BibliotecaAPI_spring.model.Usuario;
import BibliotecaAPI_spring.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService service;
	
	@GetMapping
	public PagedModel<Usuario> listar(@RequestParam(required = false) String nome, @PageableDefault(size = 5, sort = "email") Pageable pageable) {
		return new PagedModel<>(service.listarUsuarios(nome, pageable));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Usuario> buscarPorId(@PathVariable("id") Long id) {
		return ResponseEntity.ok(service.buscarID(id));
	}
	
	@PostMapping
	public ResponseEntity<Usuario> cadastrar(@RequestBody @Valid UsuarioDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.criarUsuario(dto));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Usuario> atualizar(@PathVariable("id") Long id, @RequestBody @Valid UsuarioDTO dto) {
		return ResponseEntity.ok(service.atualizarUsuario(id, dto));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Usuario> deletar(@PathVariable("id") Long id) {
		service.apagarUsuario(id);
		return ResponseEntity.noContent().build();
	}
	
}
