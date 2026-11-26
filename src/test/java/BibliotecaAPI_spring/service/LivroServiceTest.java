package BibliotecaAPI_spring.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import BibliotecaAPI_spring.DTO.LivroDTO;
import BibliotecaAPI_spring.exception.NaoEncontradoException;
import BibliotecaAPI_spring.exception.PropriedadeUnicaDuplicadaException;
import BibliotecaAPI_spring.model.Livro;
import BibliotecaAPI_spring.repository.LivroRepository;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

	@InjectMocks
	private LivroService service;
	
	@Mock
	private LivroRepository repository;
	
	private Livro livro;
	private LivroDTO dto;
	private LivroDTO dtoAtualizado;
	
	@BeforeEach
	void setUp() {
		livro = new Livro("O Pequeno Principe","Johnathan Berls", "978-3-16-148410-0", 2);
		livro.setId(1L);
		dto = new LivroDTO("Progresso e Pobreza", "Henry George", "208-2-19-275215-0", 4);
		dtoAtualizado = new LivroDTO("Riqueza das Nações","Adam Smith","925-2-08-219700-1",3);
	}
	
	@Test
	void testarListarLivros() {
		//ARRANGE
		Livro livro2 = new Livro("O Capital","Karl Marx", "237-1-20-255490-0", 3);
		
		Pageable pageable = PageRequest.of(0, 10);
		Page<Livro> pagina = new PageImpl<>(List.of(livro, livro2), pageable, 2);
		
		when(repository.findAll(pageable)).thenReturn(pagina);
		
		//ACT
		Page<Livro> lista = service.listarLivros(null, pageable);
		
		//ASSERT
		Assertions.assertNotNull(lista);
		Assertions.assertEquals(2, lista.getNumberOfElements());
		Assertions.assertEquals("O Capital", lista.getContent().get(1).getTitulo());
		verify(repository, times(1)).findAll(pageable);
	}
	
	@Test
	void testarListarLivrosPorFiltroAutor() {
		//ARRANGE
		Pageable pageable = PageRequest.of(0, 10);
		Page<Livro> pagina = new PageImpl<>(List.of(livro), pageable, 1);
		
		when(repository.findByAutorContainingIgnoreCase(livro.getAutor(), pageable)).thenReturn(pagina);
		
		//ACT
		Page<Livro> lista = service.listarLivros(livro.getAutor(), pageable);
		
		//ASSERT
		Assertions.assertNotNull(lista);
		Assertions.assertEquals("Johnathan Berls", lista.getContent().get(0).getAutor());
		verify(repository, times(1)).findByAutorContainingIgnoreCase(livro.getAutor(), pageable);
	}
	
	@Test
	void testarBuscarID() {
		//ARRANGE
		when(repository.findById(livro.getId())).thenReturn(Optional.of(livro));
		
		//ACT
		Livro valido = service.buscarID(livro.getId());
		
		//ASSERT
		Assertions.assertNotNull(valido);
		Assertions.assertEquals(1L, valido.getId());
		verify(repository, times(1)).findById(livro.getId());
	}
	
	@Test
	void testarBuscarIDInvalido() {
		//ARRANGE
		when(repository.findById(livro.getId())).thenReturn(Optional.empty());
		
		//ACT
		NaoEncontradoException invalido = Assertions.assertThrows(NaoEncontradoException.class, () -> service.buscarID(livro.getId()));
		
		//ASSERT
		Assertions.assertEquals("Livro inexistente!", invalido.getMessage());
		verify(repository, times(1)).findById(livro.getId());
	}
	
	@Test
	void testarCriarLivroComIsbnNovo() {
		//ARRANGE
		when(repository.existsByIsbn(dto.isbn())).thenReturn(false);
		when(repository.save(any(Livro.class))).thenAnswer(i -> i.getArgument(0));
		
		//ACT
		service.criarLivro(dto);
		
		//ASSERT
		ArgumentCaptor<Livro> captor = ArgumentCaptor.forClass(Livro.class);
		verify(repository).existsByIsbn(dto.isbn());
		verify(repository).save(captor.capture());
		Livro criado = captor.getValue();
		
		Assertions.assertEquals(dto.titulo(), criado.getTitulo());
		Assertions.assertEquals(dto.autor(), criado.getAutor());
		Assertions.assertEquals(dto.isbn(), criado.getIsbn());
		Assertions.assertEquals(dto.quantidadeDisponivel(), criado.getQuantidadeDisponivel());
	}
	
	@Test
	void testarCriarLivroComIsbnExistente() {
		//ARRANGE
		when(repository.existsByIsbn(dto.isbn())).thenReturn(true);
		
		//ACT
		PropriedadeUnicaDuplicadaException isbnExistente = Assertions.assertThrows(PropriedadeUnicaDuplicadaException.class, () -> service.criarLivro(dto));
		
		//ASSERT
		Assertions.assertEquals("Já existe um livro com esse ISBN!", isbnExistente.getMessage());	
		verify(repository, times(1)).existsByIsbn(dto.isbn());
		verify(repository, never()).save(any(Livro.class));
	}
	
	@Test
	void testarAtualizarLivroComIsbnValidoSemPertencimento() {
		//ARRANGE
		when(repository.findById(livro.getId())).thenReturn(Optional.of(livro));
		when(repository.existsByIsbnAndIdNot(dtoAtualizado.isbn(), livro.getId())).thenReturn(false);
		when(repository.save(any(Livro.class))).thenReturn(livro);
		
		//ACT
		Livro atualizado = service.atualizarLivro(livro.getId(), dtoAtualizado);
		
		//ASSERT
		Assertions.assertNotNull(atualizado);
		Assertions.assertEquals(dtoAtualizado.titulo(), atualizado.getTitulo());
		Assertions.assertEquals(dtoAtualizado.autor(), atualizado.getAutor());
		Assertions.assertEquals(dtoAtualizado.isbn(), atualizado.getIsbn());
		Assertions.assertEquals(dtoAtualizado.quantidadeDisponivel(), atualizado.getQuantidadeDisponivel());
		verify(repository, times(1)).findById(livro.getId());
		verify(repository, times(1)).existsByIsbnAndIdNot(dtoAtualizado.isbn(), livro.getId());
		verify(repository, times(1)).save(any(Livro.class));
	}

	@Test
	void testarAtualizarLivroComIsbnDeOutroLivro() {
		//ARRANGE
		when(repository.findById(livro.getId())).thenReturn(Optional.of(livro));
		when(repository.existsByIsbnAndIdNot(dtoAtualizado.isbn(), livro.getId())).thenReturn(true);
		
		//ACT
		PropriedadeUnicaDuplicadaException isbnJaUsado = Assertions.assertThrows(PropriedadeUnicaDuplicadaException.class, () -> service.atualizarLivro(livro.getId(), dtoAtualizado));
		
		//ASSERT
		Assertions.assertEquals("Já existe um livro com esse ISBN!", isbnJaUsado.getMessage());
		verify(repository, times(1)).findById(livro.getId());
		verify(repository, times(1)).existsByIsbnAndIdNot(dtoAtualizado.isbn(), livro.getId());
		verify(repository, never()).save(any(Livro.class));
	}
	
	@Test
	void testarAtualizarLivroComIDInvalido() {
		//ARRANGE
		when(repository.findById(livro.getId())).thenReturn(Optional.empty());
		
		//ACT
		NaoEncontradoException invalido = Assertions.assertThrows(NaoEncontradoException.class, () -> service.atualizarLivro(livro.getId(), dtoAtualizado));
		
		//ASSERT
		Assertions.assertEquals("Livro inexistente!", invalido.getMessage());
		verify(repository, times(1)).findById(livro.getId());
		verify(repository, never()).save(any(Livro.class));
	}
	
	@Test
	void testarApagarLivro() {
		//ARRANGE
		when(repository.findById(livro.getId())).thenReturn(Optional.of(livro));
		
		//ACT
		service.apagarLivro(livro.getId());
		
		//ASSERT
		verify(repository, times(1)).findById(livro.getId());
		verify(repository, times(1)).delete(livro);
	}
	
	@Test
	void testarApagarLivroComIDInvalido() {
		//ARRANGE
		when(repository.findById(livro.getId())).thenReturn(Optional.empty());
		
		//ACT
		NaoEncontradoException invalido = Assertions.assertThrows(NaoEncontradoException.class, () -> service.apagarLivro(livro.getId()));
		
		//ASSERT
		Assertions.assertEquals("Livro inexistente!", invalido.getMessage());
		verify(repository, times(1)).findById(livro.getId());
		verify(repository, never()).delete(livro);
	}
}