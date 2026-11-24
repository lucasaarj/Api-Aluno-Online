package br.com.alunoonline.api.service;

import br.com.alunoonline.api.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenServiceTest {

    private static final String SECRET = "chave-de-teste-com-mais-de-32-bytes-123456";
    private static final String ISSUER = "API ALUNO ONLINE";

    private TokenService tokenService;
    private Algorithm algorithm;

    @BeforeEach
    void configurar() {
        tokenService = new TokenService(SECRET);
        algorithm = Algorithm.HMAC256(SECRET);
    }

    @Test
    void deveRetornarSubjectDeTokenValido() {
        Usuario usuario = new Usuario();
        usuario.setLogin("aluno@email.com");

        String token = tokenService.gerarToken(usuario);

        assertEquals("aluno@email.com", tokenService.getSubject(token));
    }

    @Test
    void deveRejeitarTokenComAssinaturaAdulterada() {
        String token = gerarToken(ISSUER, Instant.now().plusSeconds(3600));
        String tokenAdulterado = token.substring(0, token.lastIndexOf('.') + 1) + "assinatura-invalida";

        assertThrows(JWTVerificationException.class,
                () -> tokenService.getSubject(tokenAdulterado));
    }

    @Test
    void deveRejeitarTokenExpirado() {
        String token = gerarToken(ISSUER, Instant.now().minusSeconds(60));

        assertThrows(JWTVerificationException.class,
                () -> tokenService.getSubject(token));
    }

    @Test
    void deveRejeitarTokenComEmissorIncorreto() {
        String token = gerarToken("OUTRO EMISSOR", Instant.now().plusSeconds(3600));

        assertThrows(JWTVerificationException.class,
                () -> tokenService.getSubject(token));
    }

    private String gerarToken(String issuer, Instant expiracao) {
        Instant agora = Instant.now();

        return JWT.create()
                .withIssuer(issuer)
                .withSubject("aluno@email.com")
                .withIssuedAt(Date.from(agora))
                .withExpiresAt(Date.from(expiracao))
                .sign(algorithm);
    }
}
