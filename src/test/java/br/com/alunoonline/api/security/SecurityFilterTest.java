package br.com.alunoonline.api.security;

import br.com.alunoonline.api.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class SecurityFilterTest {

    private SecurityFilter securityFilter;

    @BeforeEach
    void configurar() {
        securityFilter = new SecurityFilter(mock(TokenService.class));
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
}
