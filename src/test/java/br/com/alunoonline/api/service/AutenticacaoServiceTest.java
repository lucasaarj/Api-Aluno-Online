package br.com.alunoonline.api.service;

import br.com.alunoonline.api.model.Usuario;
import br.com.alunoonline.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Test
    void deveRetornarUsuarioQuandoLoginExistir() {
        Usuario usuario = new Usuario();
        usuario.setLogin("aluno@email.com");

        when(usuarioRepository.findByLoginIgnoreCase("aluno@email.com"))
                .thenReturn(Optional.of(usuario));

        assertSame(usuario, autenticacaoService.loadUserByUsername("aluno@email.com"));
        verify(usuarioRepository).findByLoginIgnoreCase("aluno@email.com");
    }

    @Test
    void deveNormalizarLoginAntesDaConsulta() {
        Usuario usuario = new Usuario();
        usuario.setLogin("aluno@email.com");

        when(usuarioRepository.findByLoginIgnoreCase("aluno@email.com"))
                .thenReturn(Optional.of(usuario));

        assertEquals("aluno@email.com",
                autenticacaoService.loadUserByUsername("  ALUNO@EMAIL.COM  ").getUsername());
        verify(usuarioRepository).findByLoginIgnoreCase("aluno@email.com");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        when(usuarioRepository.findByLoginIgnoreCase("inexistente@email.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> autenticacaoService.loadUserByUsername("inexistente@email.com"));
    }
}
