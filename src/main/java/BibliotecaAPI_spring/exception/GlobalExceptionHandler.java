package BibliotecaAPI_spring.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NaoEncontradoException.class)
	public ResponseEntity<ErroResposta> tratarRecursoNaoEncontrado(NaoEncontradoException ex) {
		ErroResposta erro = new ErroResposta(404, ex.getMessage(), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
	}
	
	@ExceptionHandler(ConflitoException.class)
	public ResponseEntity<ErroResposta> tratarConflito(ConflitoException ex) {
		ErroResposta erro = new ErroResposta(409, ex.getMessage(), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
	}
}
