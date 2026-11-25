package BibliotecaAPI_spring.exception;

public class IsbnDuplicadoException extends RuntimeException{

	public IsbnDuplicadoException(String mensagem) {
		super(mensagem);
	}
}
