package BibliotecaAPI_spring.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record LivroDTO(@NotBlank String titulo, @NotBlank String autor, @NotBlank String isbn, @PositiveOrZero int quantidadeDisponivel) {

}
