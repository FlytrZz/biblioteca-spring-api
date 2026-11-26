package BibliotecaAPI_spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import BibliotecaAPI_spring.DTO.EmprestimoDTO;
import BibliotecaAPI_spring.exception.ConflitoException;
import BibliotecaAPI_spring.exception.NaoEncontradoException;
import BibliotecaAPI_spring.model.Emprestimo;
import BibliotecaAPI_spring.model.Livro;
import BibliotecaAPI_spring.model.Status;
import BibliotecaAPI_spring.model.Usuario;
import BibliotecaAPI_spring.service.EmprestimoService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(EmprestimoController.class)
class EmprestimoControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockitoBean
	private EmprestimoService service;
	
	private Emprestimo emprestimo;
	private Livro livro;
	private Usuario usuario;
	private Emprestimo emprestimo2;
	private Livro livro2;
	private Usuario usuario2;
	private EmprestimoDTO dto;
	
	@BeforeEach
	void setUp() {
		emprestimo = new Emprestimo(LocalDate.of(2026, 11, 24),LocalDate.of(2026, 12, 01), null, Status.ATIVO);
		livro = new Livro("O Pequeno Principe","Johnathan Berls", "978-3-16-148410-0", 4);
		usuario = new Usuario("Diego", "diego@gmail.com");
		emprestimo.setId(1L);
		livro.setId(1L);
		usuario.setId(1L);
		emprestimo.setLivro(livro);
		emprestimo.setUsuario(usuario);
		
		emprestimo2 = new Emprestimo(LocalDate.of(2026, 11, 23),LocalDate.of(2026, 12, 01), null, Status.ATIVO);
		livro2 = new Livro("O Capital","Karl Marx", "237-1-20-255490-0", 3);
		usuario2 = new Usuario("Joao", "joao@gmail.com");
		emprestimo2.setId(2L);
		livro2.setId(2L);
		usuario2.setId(2L);
		emprestimo2.setLivro(livro2);
		emprestimo2.setUsuario(usuario2);
		
		dto = new EmprestimoDTO(livro.getId(), usuario.getId(), LocalDate.of(2026, 12, 01));
	}
	
	@Test
	void testar200ParaListar() throws Exception {
		//ARRANGE
		Pageable pageable = PageRequest.of(0, 10);
		Page<Emprestimo> pagina = new PageImpl<>(List.of(emprestimo, emprestimo2), pageable, 2);
		
		when(service.listarEmprestimos(any(), any(Pageable.class))).thenReturn(pagina);
		
		//ACT + ASSERT
		mvc.perform(get("/v1/emprestimos"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.length()").value(2))
		.andExpect(jsonPath("$.content[0].dataEmprestimo").value("2026-11-24"));
	}
	
	@Test
	void testar200ParaListarVazio() throws Exception {
		//ARRANGE
		Pageable pageable = PageRequest.of(0, 10);
		Page<Emprestimo> pagina = new PageImpl<>(List.of(), pageable, 0);
		
		when(service.listarEmprestimos(any(), any(Pageable.class))).thenReturn(pagina);
		
		//ACT + ASSERT
		mvc.perform(get("/v1/emprestimos"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.length()").value(0));
	}
	
	@Test
	void testar200ParaListarComFiltroStatus() throws Exception {
		//ARRANGE	
		Pageable pageable = PageRequest.of(0, 10);
		Page<Emprestimo> pagina = new PageImpl<>(List.of(emprestimo, emprestimo2), pageable, 2);
		
		when(service.listarEmprestimos(eq(Status.ATIVO), any(Pageable.class))).thenReturn(pagina);
		
		//ACT + ASSERT
		mvc.perform(get("/v1/emprestimos").param("status", "ATIVO"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.length()").value(2));
		verify(service).listarEmprestimos(eq(Status.ATIVO), any(Pageable.class));
	}
	
	@Test
	void testar400ParaListarComFiltroStatusInvalido() throws Exception {	
		//ARRANGE + ACT + ASSERT
		mvc.perform(get("/v1/emprestimos").param("status", "INVALIDO"))
		.andExpect(status().isBadRequest());
		verify(service, never()).listarEmprestimos(any(), any(Pageable.class));
	}
	
	@Test
	void testar200ParaBuscarPorID() throws Exception {
		//ARRANGE
		when(service.buscarID(emprestimo.getId())).thenReturn(emprestimo);
		
		//ACT + ASSERT
		mvc.perform(get("/v1/emprestimos/{id}", emprestimo.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.status").value("ATIVO"))
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.livro.titulo").value("O Pequeno Principe"));
	}
	
	@Test
	void testar404ParaBuscarPorId() throws Exception {
		//ARRANGE
		when(service.buscarID(99L)).thenThrow(new NaoEncontradoException("Empréstimo inexistente!"));
		
		//ACT + ASSERT
		mvc.perform(get("/v1/emprestimos/{id}", 99L))
		.andExpect(status().isNotFound());
	}
	
	@Test
	void testar400ParaBuscarIDNaoNumerico() throws Exception {
		//ARRANGE + ACT + ASSERT
		mvc.perform(get("/v1/emprestimos/{id}", "abc"))
		.andExpect(status().isBadRequest());
		verify(service, never()).buscarID(any());
	}
	
	@Test
	void testar201ParaCadastrarEmprestimo() throws Exception {
		//ARRANGE
		when(service.criarEmprestimo(any(EmprestimoDTO.class))).thenReturn(emprestimo);
		
		//ACT + ASSERT	
		mvc.perform(post("/v1/emprestimos").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").value(1));
	}
	
	@Test
	void testar404ParaCadastrarEmprestimoComLivroInexistente() throws Exception {
		//ARRANGE
		when(service.criarEmprestimo(any(EmprestimoDTO.class))).thenThrow(new NaoEncontradoException("Livro inexistente!"));
		
		//ACT + ASSERT
		mvc.perform(post("/v1/emprestimos").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
		.andExpect(status().isNotFound());
		verify(service).criarEmprestimo(any(EmprestimoDTO.class));
	}
	
	@Test
	void testar404ParaCadastrarEmprestimoComUsuarioInexistente() throws Exception {
		//ARRANGE
		when(service.criarEmprestimo(any(EmprestimoDTO.class))).thenThrow(new NaoEncontradoException("Usuário inexistente!"));
		
		//ACT + ASSERT
		mvc.perform(post("/v1/emprestimos").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
		.andExpect(status().isNotFound());
		verify(service).criarEmprestimo(any(EmprestimoDTO.class));
	}
	
	@Test
	void testar409ParaCadastrarEmprestimoSemEstoque() throws Exception {
		//ARRANGE
		when(service.criarEmprestimo(any(EmprestimoDTO.class))).thenThrow(new ConflitoException("O livro não possui mais exemplares disponiveis!"));
		
		//ACT + ASSERT
		mvc.perform(post("/v1/emprestimos").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
		.andExpect(status().isConflict());
	}
	
	@Test
	void testar409ParaCadastrarEmprestimoAcimaDoLimite() throws Exception {
		//ARRANGE
		when(service.criarEmprestimo(any(EmprestimoDTO.class))).thenThrow(new ConflitoException("Usuário já possui 3 empréstimos ativos!"));
		
		//ACT + ASSERT
		mvc.perform(post("/v1/emprestimos").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dto)))
		.andExpect(status().isConflict());
	}

	@Test
	void testar400ParaCadastrarEmprestimoComDadosInvalidos() throws Exception {
		//ARRANGE
		EmprestimoDTO dtoInvalido = new EmprestimoDTO(null, null, null);
				
		//ACT + ASSERT
		mvc.perform(post("/v1/emprestimos").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(dtoInvalido)))
		.andExpect(status().isBadRequest());
		verify(service, never()).criarEmprestimo(any());
	}
	
	@Test
	void testar200ParaDevolverEmprestimo() throws Exception {
		//ARRANGE
		Emprestimo devolvido = new Emprestimo(LocalDate.of(2026, 11, 24),LocalDate.of(2026, 12, 01), LocalDate.of(2026, 11, 29), Status.DEVOLVIDO);
		devolvido.setId(1L);
		devolvido.setLivro(livro);
		devolvido.setUsuario(usuario);
		
		when(service.devolverEmprestimo(devolvido.getId())).thenReturn(devolvido);
		
		//ACT + ASSERT
		mvc.perform(patch("/v1/emprestimos/{id}/devolucao", devolvido.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.status").value("DEVOLVIDO"))
		.andExpect(jsonPath("$.dataDevolucao").value("2026-11-29"));
	}
	
	@Test
	void testar404ParaDevolverEmprestimoInexistente() throws Exception {
		//ARRANGE
		when(service.devolverEmprestimo(99L)).thenThrow(new NaoEncontradoException("Empréstimo inexistente!"));
		
		//ACT + ASSERT
		mvc.perform(patch("/v1/emprestimos/{id}/devolucao", 99L))
		.andExpect(status().isNotFound());
	}
	
	@Test
	void testar409ParaDevolverEmprestimoJaDevolvido() throws Exception {
		//ARRANGE
		when(service.devolverEmprestimo(emprestimo.getId())).thenThrow(new ConflitoException("Empréstimo já devolvido!"));
		
		//ACT + ASSERT
		mvc.perform(patch("/v1/emprestimos/{id}/devolucao", emprestimo.getId()))
		.andExpect(status().isConflict());
	}
	
	@Test
	void testar400ParaDevolverEmprestimoComIdNaoNumerico() throws Exception {
		//ARRANGE + ACT + ASSERT
		mvc.perform(patch("/v1/emprestimos/{id}/devolucao", "abc"))
		.andExpect(status().isBadRequest());
		verify(service, never()).devolverEmprestimo(any());
	}
	
	@Test
	void testar204ParaRemoverEmprestimo() throws Exception {
		//ARRANGE
		doNothing().when(service).apagarEmprestimo(emprestimo.getId());
		
		//ACT + ASSERT
		mvc.perform(delete("/v1/emprestimos/{id}", emprestimo.getId()))
		.andExpect(status().isNoContent());
		verify(service).apagarEmprestimo(emprestimo.getId());
	}
	
	@Test
	void testar404ParaRemoverEmprestimoInexistente() throws Exception {
		//ARRANGE
		doThrow(new NaoEncontradoException("Empréstimo inexistente!")).when(service).apagarEmprestimo(99L);
		
		//ACT + ASSERT
		mvc.perform(delete("/v1/emprestimos/{id}", 99L))
		.andExpect(status().isNotFound());
	}
	
	@Test
	void testar409ParaRemoverEmprestimoAtivo() throws Exception {
		//ARRANGE
		doThrow(new ConflitoException("Empréstimo ativo não pode ser apagado!")).when(service).apagarEmprestimo(emprestimo.getId());
		
		//ACT + ASSERT
		mvc.perform(delete("/v1/emprestimos/{id}", emprestimo.getId()))
		.andExpect(status().isConflict());
	}
	
	@Test
	void testar400ParaRemoverEmprestimoComIdNaoNumerico() throws Exception {
		//ARRANGE + ACT + ASSERT
		mvc.perform(delete("/v1/emprestimos/{id}", "abc"))
		.andExpect(status().isBadRequest());
		verify(service, never()).apagarEmprestimo(any());
	}
}
