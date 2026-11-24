package br.com.alunoonline.api.controller;

import br.com.alunoonline.api.dtos.DadosAutenticacao;
import br.com.alunoonline.api.dtos.DadosTokenJWT;
import br.com.alunoonline.api.model.Usuario;
import br.com.alunoonline.api.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutenticacaoControllerTest {

    @Test
    void deveAutenticarUsuarioEGerarTokenJWT() {
        AuthenticationManager manager = mock(AuthenticationManager.class);
        TokenService tokenService = mock(TokenService.class);
        Authentication autenticacao = mock(Authentication.class);
        Usuario usuario = new Usuario();
        usuario.setLogin("aluno@email.com");

        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(autenticacao);
        when(autenticacao.getPrincipal()).thenReturn(usuario);
        when(tokenService.gerarToken(usuario)).thenReturn("jwt-gerado");

        AutenticacaoController controller = new AutenticacaoController(manager, tokenService);

        var resposta = controller.efetuarLogin(
                new DadosAutenticacao("aluno@email.com", "senha123")
        );

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(new DadosTokenJWT("jwt-gerado"), resposta.getBody());
        verify(manager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).gerarToken(usuario);
    }
}
