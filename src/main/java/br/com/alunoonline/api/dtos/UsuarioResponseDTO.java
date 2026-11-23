package br.com.alunoonline.api.dtos;

import br.com.alunoonline.api.model.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String login,
        String tipoVinculo,
        Long vinculoId
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getLogin(),
                usuario.getAluno() != null ? "ALUNO" : "PROFESSOR",
                usuario.getAluno() != null
                        ? usuario.getAluno().getId()
                        : usuario.getProfessor().getId()
        );
    }
}
