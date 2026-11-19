package BibliotecaAPI_spring.exception;

import java.time.LocalDateTime;

public record ErroResposta(int codigo, String mensagem, LocalDateTime data) {

}
