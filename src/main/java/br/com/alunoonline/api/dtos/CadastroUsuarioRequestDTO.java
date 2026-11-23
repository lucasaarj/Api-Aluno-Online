package br.com.alunoonline.api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioRequestDTO(
        @NotBlank @Email @Size(max = 254) String login,
        @NotBlank @Size(min = 8, max = 72) String senha,
        Long alunoId,
        Long professorId
) {
}
