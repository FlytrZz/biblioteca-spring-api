package BibliotecaAPI_spring.service;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import BibliotecaAPI_spring.DTO.UsuarioDTO;
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
		dto = new UsuarioDTO("Vitor", "vitor@gmail.com");
		dtoAtualizado = new UsuarioDTO("Marcelo", "marceloG52@gmail.com");
	}
	
	@Test
	void test() {
		fail("Not yet implemented");
	}

}
