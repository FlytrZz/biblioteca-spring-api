package BibliotecaAPI_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import BibliotecaAPI_spring.DTO.LivroDTO;
import BibliotecaAPI_spring.exception.NaoEncontradoException;
import BibliotecaAPI_spring.model.Livro;
import BibliotecaAPI_spring.repository.LivroRepository;
import jakarta.transaction.Transactional;

@Service
public class LivroService {

	@Autowired
	private LivroRepository repository;
	
	public Page<Livro> listarLivros(String autor, Pageable pageable) {
		if(autor == null || autor.isBlank()) {
			return repository.findAll(pageable);
		}
		return repository.findByAutorContainingIgnoreCase(autor, pageable);
	}

	public Livro buscarID(Long id) {
		return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Livro inexistente!"));
	}

	@Transactional
	public Livro criarLivro(LivroDTO dto) {
		Livro livro = new Livro(dto.titulo(), dto.autor(), dto.isbn(), dto.quantidadeDisponivel());
		return repository.save(livro);
	}

	public Livro atualizarLivro(Long id, LivroDTO dto) {
		Livro atualizado = buscarID(id);
		atualizado.setTitulo(dto.titulo());
		atualizado.setAutor(dto.autor());
		atualizado.setIsbn(dto.isbn());
		atualizado.setQuantidadeDisponivel(dto.quantidadeDisponivel());
		return repository.save(atualizado);
	}

	public void apagarLivro(Long id) {
		Livro apagado = buscarID(id);
		repository.delete(apagado);
	}

}
