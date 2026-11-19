package BibliotecaAPI_spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import BibliotecaAPI_spring.DTO.EmprestimoDTO;
import BibliotecaAPI_spring.exception.NaoEncontradoException;
import BibliotecaAPI_spring.model.Emprestimo;
import BibliotecaAPI_spring.model.Livro;
import BibliotecaAPI_spring.model.Usuario;
import BibliotecaAPI_spring.repository.EmprestimoRepository;
import BibliotecaAPI_spring.repository.LivroRepository;
import BibliotecaAPI_spring.repository.UsuarioRepository;

@Service
public class EmprestimoService {

	@Autowired
	private EmprestimoRepository repository;
	
	@Autowired
	private LivroRepository livroRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public List<Emprestimo> listarEmprestimos() {
		return repository.findAll();
	}

	public Emprestimo buscarID(Long id) {
		return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Emprestimo inexistente!"));
	}

	public Emprestimo criarEmprestimo(EmprestimoDTO dto) {
		Emprestimo emprestimo = new Emprestimo(dto.dataEmprestimo(), dto.dataPrevistaDevolucao(), dto.dataDevolucao(), dto.status());
		return repository.save(emprestimo);
	}

	public Emprestimo atualizarEmprestimo(Long id, EmprestimoDTO dto) {
		Emprestimo atualizado = buscarID(id);
		atualizado.setDataEmprestimo(dto.dataEmprestimo());
		atualizado.setDataPrevistaDevolucao(dto.dataPrevistaDevolucao());
		atualizado.setDataDevolucao(dto.dataDevolucao());
		atualizado.setStatus(dto.status());
		atualizado.setLivro(buscarLivro(dto.idLivro()));
		atualizado.setUsuario(buscarUsuario(dto.idUsuario()));
		return repository.save(atualizado);
	}

	public void apagarEmprestimo(Long id) {
		Emprestimo apagado = buscarID(id);
		repository.delete(apagado);
	}
	
	public Livro buscarLivro(Long idLivro) {
		return livroRepository.findById(idLivro).orElseThrow(() -> new NaoEncontradoException("Livro inexistente!"));
	}
	
	public Usuario buscarUsuario(Long idUsuario) {
		return usuarioRepository.findById(idUsuario).orElseThrow(() -> new NaoEncontradoException("Usuário inexistente!"));
	}
}
