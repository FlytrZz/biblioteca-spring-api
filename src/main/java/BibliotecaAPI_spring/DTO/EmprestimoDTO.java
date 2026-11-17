package BibliotecaAPI_spring.DTO;

import java.time.LocalDate;

import BibliotecaAPI_spring.model.Status;
import jakarta.validation.constraints.NotBlank;

public record EmprestimoDTO(@NotBlank Long idLivro, @NotBlank Long idUsuario, @NotBlank LocalDate dataEmprestimo, @NotBlank LocalDate dataPrevistaDevolucao, @NotBlank LocalDate dataDevolucao, @NotBlank Status status) {

}
