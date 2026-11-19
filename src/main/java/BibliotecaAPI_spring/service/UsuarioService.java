package BibliotecaAPI_spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import BibliotecaAPI_spring.DTO.UsuarioDTO;
import BibliotecaAPI_spring.exception.NaoEncontradoException;
import BibliotecaAPI_spring.model.Usuario;
import BibliotecaAPI_spring.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;
	
	public List<Usuario> listarUsuarios() {
		return repository.findAll();
	}

	public Usuario buscarID(Long id) {
		return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Usuário inexistente!"));
	}

	public Usuario criarUsuario(UsuarioDTO dto) {
		Usuario usuario = new Usuario(dto.nome(), dto.email());
		return repository.save(usuario);
	}

	public Usuario atualizarUsuario(Long id, UsuarioDTO dto) {
		Usuario atualizado = buscarID(id);
		atualizado.setNome(dto.nome());
		atualizado.setEmail(dto.email());
		return repository.save(atualizado);
	}

	public void apagarUsuario(Long id) {
		Usuario apagado = buscarID(id);
		repository.delete(apagado);
	}
}
