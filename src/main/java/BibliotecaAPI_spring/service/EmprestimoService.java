package BibliotecaAPI_spring.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import BibliotecaAPI_spring.DTO.EmprestimoDTO;
import BibliotecaAPI_spring.exception.ConflitoException;
import BibliotecaAPI_spring.exception.NaoEncontradoException;
import BibliotecaAPI_spring.model.Emprestimo;
import BibliotecaAPI_spring.model.Livro;
import BibliotecaAPI_spring.model.Status;
import BibliotecaAPI_spring.model.Usuario;
import BibliotecaAPI_spring.repository.EmprestimoRepository;
import BibliotecaAPI_spring.repository.LivroRepository;
import BibliotecaAPI_spring.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class EmprestimoService {

	private final int limite_emprestimos = 3;
	
	@Autowired
	private EmprestimoRepository repository;
	
	@Autowired
	private LivroRepository livroRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	public Page<Emprestimo> listarEmprestimos(Status status, Pageable pageable) {
		if(status == null) {
			return repository.findAll(pageable);
		}
		return repository.findByStatus(status, pageable);	
	}

	public Emprestimo buscarID(Long id) {
		return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Emprestimo inexistente!"));
	}

	@Transactional
	public Emprestimo criarEmprestimo(EmprestimoDTO dto) {
		Livro livro = buscarLivro(dto.idLivro());
		Usuario usuario = buscarUsuario(dto.idUsuario());
		if(livro.getQuantidadeDisponivel() <= 0) {
			throw new ConflitoException("O livro não tem mais exemplares disponiveis!");
		}
		long ativos = repository.countByUsuarioIdAndStatus(usuario.getId(), Status.ATIVO);
		if(ativos > limite_emprestimos) {
			throw new ConflitoException("O usuário já possui 3 empréstimos ativos");
		}
		livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
		livroRepository.save(livro);
		Emprestimo emprestimo = new Emprestimo();
		emprestimo.setLivro(livro);
		emprestimo.setUsuario(usuario);
		emprestimo.setDataEmprestimo(LocalDate.now());
		emprestimo.setDataPrevistaDevolucao(dto.dataPrevistaDevolucao());
		emprestimo.setStatus(Status.ATIVO);
		return repository.save(emprestimo);
	}
	
	@Transactional
	public Emprestimo devolverEmprestimo(Long id) {
		Emprestimo devolvido = buscarID(id);
		if(devolvido.getStatus() == Status.DEVOLVIDO) {
			throw new ConflitoException("Este empréstimo já foi devolvido!");
		}
		Livro livro = devolvido.getLivro();
		livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
		livroRepository.save(livro);
		devolvido.setDataDevolucao(LocalDate.now());
		devolvido.setStatus(Status.DEVOLVIDO);
		return repository.save(devolvido);
	}

	public void apagarEmprestimo(Long id) {
		Emprestimo apagado = buscarID(id);
		if(apagado.getStatus() == Status.ATIVO) {
			throw new ConflitoException("Não é possivel apagar um empréstimo ainda ativo, registre a devolução antes disso!");
		}
		repository.delete(apagado);
	}
	
	public Livro buscarLivro(Long idLivro) {
		return livroRepository.findById(idLivro).orElseThrow(() -> new NaoEncontradoException("Livro inexistente!"));
	}
	
	public Usuario buscarUsuario(Long idUsuario) {
		return usuarioRepository.findById(idUsuario).orElseThrow(() -> new NaoEncontradoException("Usuário inexistente!"));
	}
	
}
