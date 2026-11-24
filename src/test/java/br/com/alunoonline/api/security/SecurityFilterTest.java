package br.com.alunoonline.api.security;

import br.com.alunoonline.api.model.Usuario;
import br.com.alunoonline.api.repository.UsuarioRepository;
import br.com.alunoonline.api.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecurityFilterTest {

    private SecurityFilter securityFilter;
    private TokenService tokenService;
    private UsuarioRepository usuarioRepository;
    private FilterChain filterChain;

    @BeforeEach
    void configurar() {
        tokenService = mock(TokenService.class);
        usuarioRepository = mock(UsuarioRepository.class);
        filterChain = mock(FilterChain.class);
        securityFilter = new SecurityFilter(tokenService, usuarioRepository);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveExtrairTokenDoCabecalhoBearer() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-de-teste");

        assertEquals("token-de-teste", securityFilter.recuperarToken(request));
    }

    @Test
    void deveRetornarNuloQuandoCabecalhoNaoExistir() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertNull(securityFilter.recuperarToken(request));
    }

    @Test
    void deveRetornarNuloQuandoPrefixoBearerNaoExistir() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "token-de-teste");

        assertNull(securityFilter.recuperarToken(request));
    }

    @Test
    void deveRetornarNuloQuandoTokenEstiverVazio() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer ");

        assertNull(securityFilter.recuperarToken(request));
    }

    @Test
    void deveAutenticarUsuarioQuandoTokenForValido() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer token-valido");

        Usuario usuario = new Usuario();
        usuario.setLogin("aluno@email.com");

        when(tokenService.getSubject("token-valido")).thenReturn("aluno@email.com");
        when(usuarioRepository.findByLoginIgnoreCase("aluno@email.com"))
                .thenReturn(Optional.of(usuario));

        securityFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertSame(usuario, authentication.getPrincipal());
        assertNull(authentication.getCredentials());
        assertEquals(usuario.getAuthorities(), authentication.getAuthorities());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void naoDeveAutenticarQuandoTokenNaoForEnviado() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(usuarioRepository, never()).findByLoginIgnoreCase(org.mockito.ArgumentMatchers.anyString());
        verify(filterChain).doFilter(request, response);
    }
}
