package br.com.alunoonline.api.service;

import br.com.alunoonline.api.dtos.CadastroUsuarioRequestDTO;
import br.com.alunoonline.api.dtos.UsuarioResponseDTO;
import br.com.alunoonline.api.model.Aluno;
import br.com.alunoonline.api.model.Professor;
import br.com.alunoonline.api.model.Usuario;
import br.com.alunoonline.api.repository.AlunoRepository;
import br.com.alunoonline.api.repository.ProfessorRepository;
import br.com.alunoonline.api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            AlunoRepository alunoRepository,
            ProfessorRepository professorRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(CadastroUsuarioRequestDTO dados) {
        boolean possuiAluno = dados.alunoId() != null;
        boolean possuiProfessor = dados.professorId() != null;

        if (possuiAluno == possuiProfessor) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe alunoId ou professorId, mas não ambos."
            );
        }

        String login = dados.login().trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByLoginIgnoreCase(login)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um usuário cadastrado com este e-mail."
            );
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setSenha(passwordEncoder.encode(dados.senha()));

        if (possuiAluno) {
            Aluno aluno = alunoRepository.findById(dados.alunoId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Aluno não encontrado."
                    ));
            validarEmailVinculado(login, aluno.getEmail());
            usuario.setAluno(aluno);
        } else {
            Professor professor = professorRepository.findById(dados.professorId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Professor não encontrado."
                    ));
            validarEmailVinculado(login, professor.getEmail());
            usuario.setProfessor(professor);
        }

        return new UsuarioResponseDTO(usuarioRepository.save(usuario));
    }

    private void validarEmailVinculado(String login, String emailAcademico) {
        if (emailAcademico == null || !login.equalsIgnoreCase(emailAcademico.trim())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O login deve ser igual ao e-mail do aluno ou professor vinculado."
            );
        }
    }
}
