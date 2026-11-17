package BibliotecaAPI_spring.DTO;

import jakarta.validation.constraints.NotBlank;

public record LivroDTO(@NotBlank String titulo, @NotBlank String autor, @NotBlank String isbn, int quantidadeDisponivel) {

}
