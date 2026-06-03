package br.com.alunoonline.api.service;

import br.com.alunoonline.api.dtos.AtualizarNotasRequestDTO;
import br.com.alunoonline.api.model.Aluno;
import br.com.alunoonline.api.model.Disciplina;
import br.com.alunoonline.api.model.MatriculaAluno;
import br.com.alunoonline.api.model.Professor;
import br.com.alunoonline.api.enums.MatriculaAlunoStatusEnum;
import br.com.alunoonline.api.repository.MatriculaAlunoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaAlunoServiceTest {

    @Mock
    MatriculaAlunoRepository matriculaAlunoRepository;

    @InjectMocks
    MatriculaAlunoService matriculaService;

    @Test
    void trancarMatricula_deveAlterarParaTrancadoQuandoMatriculado() {
        MatriculaAluno m = new MatriculaAluno(1L, new Aluno(), new Disciplina(), null, null, MatriculaAlunoStatusEnum.MATRICULADO);
        when(matriculaAlunoRepository.findById(1L)).thenReturn(Optional.of(m));

        matriculaService.trancarMatricula(1L);

        ArgumentCaptor<MatriculaAluno> captor = ArgumentCaptor.forClass(MatriculaAluno.class);
        verify(matriculaAlunoRepository).save(captor.capture());
        assertEquals(MatriculaAlunoStatusEnum.TRANCADO, captor.getValue().getStatus());
    }

    @Test
    void trancarMatricula_quandoNaoMatriculado_deveLancarBadRequest() {
        MatriculaAluno m = new MatriculaAluno(1L, new Aluno(), new Disciplina(), null, null, MatriculaAlunoStatusEnum.REPROVADO);
        when(matriculaAlunoRepository.findById(1L)).thenReturn(Optional.of(m));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> matriculaService.trancarMatricula(1L));
        assertEquals("Só é possível trancar com status MATRICULADO", ex.getReason());
    }

    @Test
    void atualizarNotas_deveCalcularMediaEAjustarStatus() {
        MatriculaAluno m = new MatriculaAluno(1L, new Aluno(), new Disciplina(), null, null, MatriculaAlunoStatusEnum.MATRICULADO);
        when(matriculaAlunoRepository.findById(1L)).thenReturn(Optional.of(m));

        AtualizarNotasRequestDTO dto = new AtualizarNotasRequestDTO();
        dto.setNota1(8.0);
        dto.setNota2(6.0);

        matriculaService.atualizarNotas(1L, dto);

        ArgumentCaptor<MatriculaAluno> captor = ArgumentCaptor.forClass(MatriculaAluno.class);
        verify(matriculaAlunoRepository).save(captor.capture());
        MatriculaAluno salvo = captor.getValue();
        assertEquals(8.0, salvo.getNota1());
        assertEquals(6.0, salvo.getNota2());
        assertEquals(MatriculaAlunoStatusEnum.APROVADO, salvo.getStatus());
    }

    @Test
    void emitirHistorico_quandoNenhumaMatricula_deveLancar404() {
        when(matriculaAlunoRepository.findByAlunoId(99L)).thenReturn(List.of());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> matriculaService.emitirHistorico(99L));
        assertEquals("Nenhuma matrícula encontrada para esse aluno", ex.getReason());
    }

    @Test
    void emitirHistorico_deveConstruirHistoricoCorreto() {
        Aluno aluno = new Aluno(1L, "AlunoX", "123", "a@x.com");
        Professor prof = new Professor(1L, "ProfX", "000", "p@x.com");
        Disciplina disc = new Disciplina(1L, "Matematica", 60, prof);
        MatriculaAluno m = new MatriculaAluno(1L, aluno, disc, 5.0, 7.0, MatriculaAlunoStatusEnum.REPROVADO);

        when(matriculaAlunoRepository.findByAlunoId(1L)).thenReturn(List.of(m));

        var historico = matriculaService.emitirHistorico(1L);

        assertEquals("AlunoX", historico.getNomeAluno());
        assertEquals("a@x.com", historico.getEmailAluno());
        assertEquals("123", historico.getCpfAluno());
        assertFalse(historico.getDisciplinas().isEmpty());
        var discResp = historico.getDisciplinas().get(0);
        assertEquals("Matematica", discResp.getNomeDisciplina());
        assertEquals("ProfX", discResp.getNomeProfessor());
        assertEquals(5.0, discResp.getNota1());
        assertEquals(7.0, discResp.getNota2());
    }
}
