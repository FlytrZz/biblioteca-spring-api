package BibliotecaAPI_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import BibliotecaAPI_spring.DTO.UsuarioDTO;
import BibliotecaAPI_spring.exception.NaoEncontradoException;
import BibliotecaAPI_spring.exception.PropriedadeUnicaDuplicadaException;
import BibliotecaAPI_spring.model.Usuario;
import BibliotecaAPI_spring.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;
	
	public Page<Usuario> listarUsuarios(String nome, Pageable pageable) {
		if(nome == null || nome.isBlank()) {
			return repository.findAll(pageable);
		}
		return repository.findByNomeContainingIgnoreCase(nome, pageable);
	}

	public Usuario buscarID(Long id) {
		return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Usuário inexistente!"));
	}

	@Transactional
	public Usuario criarUsuario(UsuarioDTO dto) {
		if(repository.existsByEmail(dto.email())) {
			throw new PropriedadeUnicaDuplicadaException("Esse email já pertence a uma pessoa!");
		}
		Usuario usuario = new Usuario(dto.nome(), dto.email());
		return repository.save(usuario);
	}

	@Transactional
	public Usuario atualizarUsuario(Long id, UsuarioDTO dto) {
		Usuario atualizado = buscarID(id);
		if(repository.existsByEmailAndIdNot(dto.email(), id)) {
			throw new PropriedadeUnicaDuplicadaException("Esse email já pertence a uma pessoa!");
		}
		atualizado.setNome(dto.nome());
		atualizado.setEmail(dto.email());
		return repository.save(atualizado);
	}

	@Transactional
	public void apagarUsuario(Long id) {
		Usuario apagado = buscarID(id);
		repository.delete(apagado);
	}
}
