package br.com.alunoonline.api.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioTest {

    @Test
    void deveRetornarAutoridadeRoleUser() {
        Usuario usuario = new Usuario();

        assertEquals(
                java.util.List.of(new SimpleGrantedAuthority("ROLE_USER")),
                usuario.getAuthorities()
        );
    }
}
