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

import BibliotecaAPI_spring.DTO.UsuarioDTO;
import BibliotecaAPI_spring.exception.NaoEncontradoException;
import BibliotecaAPI_spring.exception.PropriedadeUnicaDuplicadaException;
import BibliotecaAPI_spring.model.Usuario;
import BibliotecaAPI_spring.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

	@InjectMocks
	private UsuarioService service;
	
	@Mock
	private UsuarioRepository repository;
	
	private Usuario usuario;
	private UsuarioDTO dto;
	private UsuarioDTO dtoAtualizado;
	
	@BeforeEach
	void setUp() {
		usuario = new Usuario("Marcelo", "marcelo@gmail.com");
		usuario.setId(1L);
		dto = new UsuarioDTO("Vitor", "vitor@gmail.com");
		dtoAtualizado = new UsuarioDTO("Marcelo Gomes", "marceloG52@gmail.com");
	}
	
	@Test
	void testarListarUsuarios() {
		//ARRANGE
		Usuario usuario2 = new Usuario("Matias", "matias@gmail.com");
		
		Pageable pageable = PageRequest.of(0, 10);
		Page<Usuario> pagina = new PageImpl<>(List.of(usuario, usuario2), pageable, 2);
		
		when(repository.findAll(pageable)).thenReturn(pagina);
		
		//ACT
		Page<Usuario> lista = service.listarUsuarios(null, pageable);
		
		//ASSERT
		Assertions.assertEquals(2, lista.getNumberOfElements());
		Assertions.assertEquals("matias@gmail.com", lista.getContent().get(1).getEmail());
		verify(repository, times(1)).findAll(pageable);
	}
	
	@Test
	void testarListarUsuariosComFiltroNome() {
		//ARRANGE
		Pageable pageable = PageRequest.of(0, 10);
		Page<Usuario> pagina = new PageImpl<>(List.of(usuario), pageable, 1);
		
		when(repository.findByNomeContainingIgnoreCase(usuario.getNome(), pageable)).thenReturn(pagina);
		
		//ACT
		Page<Usuario> lista = service.listarUsuarios(usuario.getNome(), pageable);
		
		//ASSERT
		Assertions.assertNotNull(lista);
		Assertions.assertEquals("Marcelo", lista.getContent().get(0).getNome());
		verify(repository, times(1)).findByNomeContainingIgnoreCase(usuario.getNome(), pageable);
	}

	@Test
	void testarBuscarID() {
		//ARRANGE
		when(repository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
		
		//ACT
		Usuario valido = service.buscarID(usuario.getId());
		
		//ASSERT
		Assertions.assertNotNull(valido);
		Assertions.assertEquals(1L, valido.getId());
		verify(repository, times(1)).findById(usuario.getId());
	}
	
	@Test
	void testarBuscarIDInvalido() {
		//ARRANGE
		when(repository.findById(usuario.getId())).thenReturn(Optional.empty());
		
		//ACT
		NaoEncontradoException invalido = Assertions.assertThrows(NaoEncontradoException.class, () -> service.buscarID(usuario.getId()));
		
		//ASSERT
		Assertions.assertEquals("Usuário inexistente!", invalido.getMessage());
		verify(repository, times(1)).findById(usuario.getId());
	}
	
	@Test
	void testarCriarUsuarioComEmailNovo() {
		//ARRANGE
		when(repository.existsByEmail(dto.email())).thenReturn(false);
		when(repository.save(any(Usuario.class))).thenReturn(usuario);
		
		//ACT
		service.criarUsuario(dto);
		
		//ASSERT
		ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
		verify(repository).existsByEmail(dto.email());
		verify(repository).save(captor.capture());
		Usuario criado = captor.getValue();
		
		Assertions.assertEquals(dto.nome(), criado.getNome());
		Assertions.assertEquals(dto.email(), criado.getEmail());
	}
	
	@Test
	void testarCriarUsuarioComEmailExistente() {
		//ARRANGE
		when(repository.existsByEmail(dto.email())).thenReturn(true);
		
		//ACT
		PropriedadeUnicaDuplicadaException emailExistente = Assertions.assertThrows(PropriedadeUnicaDuplicadaException.class, () -> service.criarUsuario(dto));
		
		//ASSERT
		Assertions.assertEquals("Esse email já pertence a uma pessoa!", emailExistente.getMessage());
		verify(repository, times(1)).existsByEmail(dto.email());
		verify(repository, never()).save(any(Usuario.class));
	}
	
	@Test
	void testarAtualizarUsuarioComEmailValidoSemPertencimento() {
		//ARRANGE
		when(repository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
		when(repository.existsByEmailAndIdNot(dtoAtualizado.email(), usuario.getId())).thenReturn(false);
		when(repository.save(any(Usuario.class))).thenReturn(usuario);
		
		//ACT
		Usuario resultado = service.atualizarUsuario(usuario.getId(), dtoAtualizado);
		
		//ASSERT
		ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
		verify(repository).save(captor.capture());
		Usuario atualizado = captor.getValue();
		
		Assertions.assertSame(usuario, resultado);
		Assertions.assertEquals(dtoAtualizado.nome(), atualizado.getNome());
		Assertions.assertEquals(dtoAtualizado.email(), atualizado.getEmail());
		verify(repository, times(1)).findById(usuario.getId());
		verify(repository, times(1)).existsByEmailAndIdNot(dtoAtualizado.email(), usuario.getId());
	}
	
	@Test
	void testarAtualizarUsuarioComEmailDeOutroUsuario() {
		//ARRANGE
		when(repository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
		when(repository.existsByEmailAndIdNot(dtoAtualizado.email(), usuario.getId())).thenReturn(true);
		
		//ACT
		PropriedadeUnicaDuplicadaException emailExistente = Assertions.assertThrows(PropriedadeUnicaDuplicadaException.class, () -> service.atualizarUsuario(usuario.getId(), dtoAtualizado));
		
		//ASSERT
		Assertions.assertEquals("Esse email já pertence a uma pessoa!", emailExistente.getMessage());
		Assertions.assertEquals("Marcelo", usuario.getNome());
		Assertions.assertEquals("marcelo@gmail.com", usuario.getEmail());
		verify(repository, times(1)).findById(usuario.getId());
		verify(repository, times(1)).existsByEmailAndIdNot(dtoAtualizado.email(), usuario.getId());
		verify(repository, never()).save(any(Usuario.class));
	}
	
	@Test
	void testarAtualizarUsuarioComIDInvalido() {
		//ARRANGE
		when(repository.findById(usuario.getId())).thenReturn(Optional.empty());
		
		//ACT
		NaoEncontradoException invalido = Assertions.assertThrows(NaoEncontradoException.class, () -> service.atualizarUsuario(usuario.getId(), dtoAtualizado));
		
		//ASSERT
		Assertions.assertEquals("Usuário inexistente!", invalido.getMessage());
		verify(repository, times(1)).findById(usuario.getId());
		verify(repository, never()).save(any(Usuario.class));
	}
	
	@Test
	void testarApagarUsuario() {
		//ARRANGE
		when(repository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
		
		//ACT
		service.apagarUsuario(usuario.getId());
		
		//ASSERT
		verify(repository, times(1)).delete(usuario);
	}
	
	@Test
	void testarApagarUsuarioComIDInvalido() {
		//ARRANGE
		when(repository.findById(usuario.getId())).thenReturn(Optional.empty());
		
		//ACT
		NaoEncontradoException invalido = Assertions.assertThrows(NaoEncontradoException.class, () -> service.apagarUsuario(usuario.getId()));
		
		//ASSERT
		Assertions.assertEquals("Usuário inexistente!", invalido.getMessage());
		verify(repository, times(1)).findById(usuario.getId());
		verify(repository, never()).delete(usuario);
	}
}
