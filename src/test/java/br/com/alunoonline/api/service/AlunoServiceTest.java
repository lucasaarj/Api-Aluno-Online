package br.com.alunoonline.api.service;

import br.com.alunoonline.api.model.Aluno;
import br.com.alunoonline.api.repository.AlunoRepository;
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
class AlunoServiceTest {

    @Mock
    AlunoRepository alunoRepository;

    @InjectMocks
    AlunoService alunoService;

    @Test
    void criarAluno_deveChamarSave() {
        Aluno a = new Aluno(null, "João", "11122233344", "joao@email.com");
        alunoService.criarAluno(a);
        verify(alunoRepository, times(1)).save(a);
    }

    @Test
    void listarTodosAlunos_retornaLista() {
        when(alunoRepository.findAll()).thenReturn(List.of(new Aluno(1L, "A", "1", "a@a.com")));
        List<Aluno> result = alunoService.listarTodosAlunos();
        assertFalse(result.isEmpty());
        verify(alunoRepository).findAll();
    }

    @Test
    void buscarAlunoPorId_retornaOptional() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(new Aluno(1L, "A", "1", "a@a.com")));
        Optional<Aluno> opt = alunoService.buscarAlunoPorId(1L);
        assertTrue(opt.isPresent());
    }

    @Test
    void atualizarAlunoPorId_quandoNaoExiste_deveLancar404() {
        when(alunoRepository.findById(10L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> alunoService.atualizarAlunoPorId(10L, new Aluno()));
        assertEquals("Aluno não encontrado", ex.getReason());
    }

    @Test
    void atualizarAlunoPorId_deveAtualizarCamposESalvar() {
        Aluno existente = new Aluno(1L, "Old", "000", "old@x.com");
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(existente));

        Aluno novo = new Aluno(null, "New", "999", "new@x.com");
        alunoService.atualizarAlunoPorId(1L, novo);

        ArgumentCaptor<Aluno> captor = ArgumentCaptor.forClass(Aluno.class);
        verify(alunoRepository).save(captor.capture());
        Aluno salvo = captor.getValue();
        assertEquals("New", salvo.getNome());
        assertEquals("999", salvo.getCpf());
        assertEquals("new@x.com", salvo.getEmail());
    }

    @Test
    void deletarAlunoPorId_quandoNaoExiste_deveLancar404() {
        when(alunoRepository.existsById(5L)).thenReturn(false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> alunoService.deletarAlunoPorId(5L));
        assertEquals("Aluno não encontrado", ex.getReason());
    }
}