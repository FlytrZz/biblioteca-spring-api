package BibliotecaAPI_spring.DTO;

import java.time.LocalDate;

import BibliotecaAPI_spring.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmprestimoDTO(@NotNull Long idLivro, @NotNull Long idUsuario, @NotNull LocalDate dataPrevistaDevolucao) {

}
