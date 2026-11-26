package BibliotecaAPI_spring.exception;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
	
	@ExceptionHandler(PropertyReferenceException.class)
	public ResponseEntity<ErroResposta> tratarPropriedadeNaoReferenciada(PropertyReferenceException ex){
		String mensagem = "Propriedade não referenciada: "+ex.getPropertyName();
		ErroResposta erro = new ErroResposta(400, mensagem, LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErroResposta> tratarConversãoInvalida(MethodArgumentTypeMismatchException ex){
		String mensagem = "Conversão do "+ex.getPropertyName()+" inválida";
		ErroResposta erro = new ErroResposta(400, mensagem, LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	}
	
	@ExceptionHandler(PropriedadeUnicaDuplicadaException.class)
	public ResponseEntity<ErroResposta> tratarPropriedadeUnicaDuplicada(PropriedadeUnicaDuplicadaException ex) {
		ErroResposta erro = new ErroResposta(409, ex.getMessage(), LocalDateTime.now());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
	}
}
