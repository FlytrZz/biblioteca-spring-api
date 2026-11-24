package BibliotecaAPI_spring.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
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

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {
	
	@InjectMocks
	private EmprestimoService service;
	
	@Mock
	private EmprestimoRepository repository;
	
	@Mock
	private UsuarioRepository usuarioRepository;
	
	@Mock
	private LivroRepository livroRepository;
	
	private Emprestimo emprestimo;
	private Usuario usuario;
	private Livro livro;
	private EmprestimoDTO dto;

	@BeforeEach
	void setUp() {
		emprestimo = new Emprestimo(LocalDate.of(2026, 11, 24),LocalDate.of(2026, 12, 01), LocalDate.of(2026, 11, 29), Status.ATIVO);
		livro = new Livro("O Pequeno Principe","Johnathan Berls", "978-3-16-148410-0", 4);
		usuario = new Usuario("Diego", "diego@gmail.com");
		emprestimo.setId(1L);
		livro.setId(1L);
		usuario.setId(1L);
		emprestimo.setLivro(livro);
		emprestimo.setUsuario(usuario);
		
		dto = new EmprestimoDTO(livro.getId(), usuario.getId(), LocalDate.of(2026, 12, 01));
	}
	
	@Test
	void testarListarEmprestimos() {
		//ARRANGE
		Emprestimo emprestimo2 = new Emprestimo(LocalDate.of(2026, 11, 23),LocalDate.of(2026, 12, 01), LocalDate.of(2026, 11, 29), Status.ATIVO);
		Livro livro2 = new Livro("O Capital","Karl Marx", "237-1-20-255490-0", 3);
		Usuario usuario2 = new Usuario("Joao", "joao@gmail.com");
		emprestimo2.setId(2L);
		livro2.setId(2L);
		usuario2.setId(2L);
		emprestimo2.setLivro(livro2);
		emprestimo2.setUsuario(usuario2);
		
		Pageable pageable = PageRequest.of(0, 10);
		Page<Emprestimo> pagina = new PageImpl<>(List.of(emprestimo, emprestimo2), pageable, 2);
		
		when(repository.findAll(pageable)).thenReturn(pagina);
		
		//ACT
		Page<Emprestimo> resultado = service.listarEmprestimos(null, pageable);
		
		//ASSERT
		Assertions.assertEquals(2, resultado.getNumberOfElements());
		Assertions.assertNotNull(resultado);
		verify(repository, times(1)).findAll(pageable);
	}
	
	@Test
	void testarListarEmprestimosComFiltroStatus() {
		//ARRANGE
		Pageable pageable = PageRequest.of(0, 10);
		Page<Emprestimo> pagina = new PageImpl<>(List.of(emprestimo), pageable, 1);
		
		when(repository.findByStatus(emprestimo.getStatus(), pageable)).thenReturn(pagina);
		
		//ACT
		Page<Emprestimo> resultado = service.listarEmprestimos(emprestimo.getStatus(), pageable);
		
		//ASSERT
		Assertions.assertNotNull(resultado);
		Assertions.assertEquals(1, resultado.getNumberOfElements());
		Assertions.assertEquals(Status.ATIVO, resultado.getContent().get(0).getStatus());
		verify(repository, times(1)).findByStatus(emprestimo.getStatus(), pageable);
		verify(repository, never()).findAll(any(Pageable.class));
	}
	
	@Test
	void testarBuscarID() {
		//ARRANGE
		when(repository.findById(emprestimo.getId())).thenReturn(Optional.of(emprestimo));
		
		//ACT
		Emprestimo achado = service.buscarID(emprestimo.getId());
		
		//ASSERT
		Assertions.assertNotNull(achado);
		Assertions.assertEquals(emprestimo.getId(), achado.getId());
		verify(repository, times(1)).findById(emprestimo.getId());
	}
	
	@Test
	void testarBuscarIDInvalido() {
		//ARRANGE
		when(repository.findById(emprestimo.getId())).thenReturn(Optional.empty());
		
		//ACT
		Exception naoAchado = assertThrows(NaoEncontradoException.class, () -> service.buscarID(emprestimo.getId()));
		
		//ASSERT
		Assertions.assertEquals("Empréstimo inexistente!", naoAchado.getMessage());
		verify(repository, times(1)).findById(emprestimo.getId());
	}
	
	@Test
	void testarCriarEmprestimo() {
		//ARRANGE
		when(livroRepository.findById(livro.getId())).thenReturn(Optional.of(livro));
		when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
		when(repository.save(any(Emprestimo.class))).thenReturn(emprestimo);
		
		//ACT
		Emprestimo criado = service.criarEmprestimo(dto);
		
		//ASSERT
		ArgumentCaptor<Emprestimo> captor = ArgumentCaptor.forClass(Emprestimo.class);
		verify(repository, times(1)).save(captor.capture());
		Emprestimo salvo = captor.getValue();
		
		Assertions.assertNotNull(criado);
		Assertions.assertEquals(livro, salvo.getLivro());
		Assertions.assertEquals(usuario, salvo.getUsuario());
		Assertions.assertEquals(Status.ATIVO, salvo.getStatus());
		Assertions.assertEquals(LocalDate.now(), salvo.getDataEmprestimo());
		Assertions.assertEquals(dto.dataPrevistaDevolucao(), salvo.getDataPrevistaDevolucao());
		Assertions.assertEquals(3, livro.getQuantidadeDisponivel());
		verify(livroRepository, times(1)).save(livro);
	}
	
	@Test
	void testarCriarEmprestimoSemLivro() {
		//ARRANGE
		when(livroRepository.findById(livro.getId())).thenReturn(Optional.empty());
				
		//ACT
		Exception semLivro = assertThrows(NaoEncontradoException.class, () -> service.criarEmprestimo(dto));
				
		//ASSERT
		Assertions.assertEquals("Livro inexistente!", semLivro.getMessage());
		verify(usuarioRepository, never()).findById(any());
		verify(livroRepository, never()).save(any(Livro.class));
		verify(repository, never()).save(any(Emprestimo.class));
	}
	
	@Test
	void testarCriarEmprestimoSemUsuario() {
		//ARRANGE
		when(livroRepository.findById(livro.getId())).thenReturn(Optional.of(livro));
		when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());
				
		//ACT
		Exception semUsuario = assertThrows(NaoEncontradoException.class, () -> service.criarEmprestimo(dto));
				
		//ASSERT
		Assertions.assertEquals("Usuário inexistente!", semUsuario.getMessage());
		verify(livroRepository, never()).save(any(Livro.class));
		verify(repository, never()).save(any(Emprestimo.class));
	}
	
	@Test
	void testarCriarEmprestimoSemExemplaresDisponiveis() {
		//ARRANGE
		livro.setQuantidadeDisponivel(0);
		
		when(livroRepository.findById(livro.getId())).thenReturn(Optional.of(livro));
		when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
		
		//ACT
		ConflitoException exemplaresIndisponiveis = assertThrows(ConflitoException.class, () -> service.criarEmprestimo(dto));
		
		//ASSERT
		Assertions.assertEquals("O livro não tem mais exemplares disponiveis!", exemplaresIndisponiveis.getMessage());
		verify(repository, never()).save(any(Emprestimo.class));
		verify(livroRepository, never()).save(any(Livro.class));
	}
	
	@Test
	void testarCriarEmprestimoComLimiteExato() {
		//ARRANGE
		Long ativos = 3L;
		
		when(livroRepository.findById(livro.getId())).thenReturn(Optional.of(livro));
		when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
		when(repository.countByUsuarioIdAndStatus(usuario.getId(), Status.ATIVO)).thenReturn(ativos);
		
		//ACT
		ConflitoException limiteExato = assertThrows(ConflitoException.class, () -> service.criarEmprestimo(dto));
		
		//ASSERT
		Assertions.assertEquals("O usuário já possui 3 empréstimos ativos", limiteExato.getMessage());
		verify(repository, never()).save(any(Emprestimo.class));
		verify(livroRepository, never()).save(any(Livro.class));
	}
	
	@Test
	void testarCriarEmprestimoComLimiteAcima() {
		//ARRANGE
		Long ativos = 4L;
		
		when(livroRepository.findById(livro.getId())).thenReturn(Optional.of(livro));
		when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
		when(repository.countByUsuarioIdAndStatus(usuario.getId(), Status.ATIVO)).thenReturn(ativos);
		
		//ACT
		ConflitoException limiteAcima = assertThrows(ConflitoException.class, () -> service.criarEmprestimo(dto));
		
		//ASSERT
		Assertions.assertEquals("O usuário já possui 3 empréstimos ativos", limiteAcima.getMessage());
		verify(repository, never()).save(any(Emprestimo.class));
		verify(livroRepository, never()).save(any(Livro.class));
	}
	
	@Test
	void testarCriarEmprestimoComLimiteAbaixo() {
		//ARRANGE
		Long ativos = 2L;
		
		when(livroRepository.findById(livro.getId())).thenReturn(Optional.of(livro));
		when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
		when(repository.countByUsuarioIdAndStatus(usuario.getId(), Status.ATIVO)).thenReturn(ativos);
		when(repository.save(any(Emprestimo.class))).thenReturn(emprestimo);
		
		//ACT
		Emprestimo criado = service.criarEmprestimo(dto);
		
		//ASSERT
		Assertions.assertNotNull(criado);
		verify(repository, times(1)).save(any(Emprestimo.class));
		verify(livroRepository, times(1)).save(livro);
	}
	
	@Test
	void testarDevolverEmprestimo() {
		//ARRANGE
		int estoqueAntigo = livro.getQuantidadeDisponivel();
		emprestimo.setStatus(Status.ATIVO);
		emprestimo.setLivro(livro);
		
		when(repository.findById(emprestimo.getId())).thenReturn(Optional.of(emprestimo));
		when(repository.save(any(Emprestimo.class))).thenReturn(emprestimo);
		
		//ACT
		Emprestimo devolvido = service.devolverEmprestimo(emprestimo.getId());
		
		//ASSERT
		Assertions.assertNotNull(devolvido);
		Assertions.assertNotNull(devolvido.getDataDevolucao());
		Assertions.assertEquals(Status.DEVOLVIDO, devolvido.getStatus());
		Assertions.assertEquals(estoqueAntigo+1, livro.getQuantidadeDisponivel());
		verify(livroRepository, times(1)).save(livro);
		verify(repository, times(1)).save(emprestimo);
	}
	
	@Test
	void testarDevolverEmprestimoJaDevolvido() {
		//ARRANGE
		int estoqueAntigo = livro.getQuantidadeDisponivel();
		emprestimo.setStatus(Status.DEVOLVIDO);
		emprestimo.setLivro(livro);
		
		when(repository.findById(emprestimo.getId())).thenReturn(Optional.of(emprestimo));
		
		//ACT
		ConflitoException devolvidoAntes = assertThrows(ConflitoException.class, () -> service.devolverEmprestimo(emprestimo.getId()));
		
		//ASSERT
		Assertions.assertEquals("Este empréstimo já foi devolvido!", devolvidoAntes.getMessage());
		Assertions.assertEquals(estoqueAntigo, livro.getQuantidadeDisponivel());
		verify(livroRepository, never()).save(any(Livro.class));
		verify(repository, never()).save(any(Emprestimo.class));
	}
	
	@Test
	void testarDevolverEmprestimoComIDInvalido() {
		//ARRANGE
		when(repository.findById(emprestimo.getId())).thenReturn(Optional.empty());
		
		//ACT
		NaoEncontradoException naoEncontrado = assertThrows(NaoEncontradoException.class, () -> service.devolverEmprestimo(emprestimo.getId()));
		
		//ASSERT
		Assertions.assertEquals("Empréstimo inexistente!", naoEncontrado.getMessage());
		verify(livroRepository, never()).save(any(Livro.class));
		verify(repository, never()).save(any(Emprestimo.class));
	}
	
	@Test
	void testarApagarEmprestimo() {
		//ARRANGE
		Emprestimo emprestimoAcabado = new Emprestimo(LocalDate.of(2026, 11, 23),LocalDate.of(2026, 12, 01), LocalDate.of(2026, 11, 29), Status.DEVOLVIDO);
		emprestimoAcabado.setId(2L);
		emprestimoAcabado.setLivro(livro);
		emprestimoAcabado.setUsuario(usuario);
		
		when(repository.findById(emprestimoAcabado.getId())).thenReturn(Optional.of(emprestimoAcabado));
		
		//ACT
		service.apagarEmprestimo(emprestimoAcabado.getId());
		
		//ASSERT
		verify(repository, times(1)).delete(emprestimoAcabado);
	}
	
	@Test
	void testarApagarEmprestimoAtivo() {
		//ARRANGE
		when(repository.findById(emprestimo.getId())).thenReturn(Optional.of(emprestimo));
		
		//ACT
		ConflitoException ativo = assertThrows(ConflitoException.class, () -> service.apagarEmprestimo(emprestimo.getId()));
		
		//ASSERT
		Assertions.assertEquals("Não é possivel apagar um empréstimo ainda ativo, registre a devolução antes disso!", ativo.getMessage());
		verify(repository, never()).delete(emprestimo);
	}
	
	@Test
	void testarApagarEmprestimoComIDInvalido() {
		//ARRANGE
		when(repository.findById(emprestimo.getId())).thenReturn(Optional.empty());
		
		//ACT
		NaoEncontradoException naoEncontrado = assertThrows(NaoEncontradoException.class, () -> service.apagarEmprestimo(emprestimo.getId()));
		
		//ASSERT
		Assertions.assertEquals("Empréstimo inexistente!", naoEncontrado.getMessage());
		verify(repository, never()).delete(any(Emprestimo.class));
	}
}
