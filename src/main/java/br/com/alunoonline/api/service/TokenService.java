package br.com.alunoonline.api.service;

import br.com.alunoonline.api.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class TokenService {

    private static final String ISSUER = "API ALUNO ONLINE";
    private static final Duration EXPIRATION = Duration.ofHours(2);

    private final Algorithm algorithm;

    public TokenService(@Value("${api.security.token.secret}") String secret) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (secretBytes.length < 32) {
            throw new IllegalStateException(
                    "A chave JWT deve possuir pelo menos 32 bytes para o algoritmo HMAC256."
            );
        }

        this.algorithm = Algorithm.HMAC256(secretBytes);
    }

    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(usuario.getUsername())
                .withIssuedAt(Date.from(agora))
                .withExpiresAt(Date.from(agora.plus(EXPIRATION)))
                .sign(algorithm);
    }
}
