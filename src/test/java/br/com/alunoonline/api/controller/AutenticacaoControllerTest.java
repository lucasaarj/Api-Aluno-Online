package br.com.alunoonline.api.controller;

import br.com.alunoonline.api.dtos.DadosAutenticacao;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AutenticacaoControllerTest {

    @Test
    void deveDelegarAutenticacaoAoAuthenticationManagerERetornarOk() {
        AuthenticationManager manager = mock(AuthenticationManager.class);
        AutenticacaoController controller = new AutenticacaoController(manager);

        var resposta = controller.efetuarLogin(
                new DadosAutenticacao("aluno@email.com", "senha123")
        );

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(manager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
