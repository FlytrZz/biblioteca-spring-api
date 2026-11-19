package BibliotecaAPI_spring.exception;

public class ConflitoException extends RuntimeException{

	public ConflitoException(String mensagem) {
		super(mensagem);
	}
}
