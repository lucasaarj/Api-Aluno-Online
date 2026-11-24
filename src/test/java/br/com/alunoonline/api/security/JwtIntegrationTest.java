package br.com.alunoonline.api.security;

import br.com.alunoonline.api.model.Usuario;
import br.com.alunoonline.api.service.AlunoService;
import br.com.alunoonline.api.service.TokenService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "api.security.token.secret=chave-de-teste-com-mais-de-32-bytes-123456")
@AutoConfigureMockMvc
class JwtIntegrationTest {

    private static final String LOGIN = "aluno@email.com";
    private static final String SENHA = "senha123";
    private static final String SECRET = "chave-de-teste-com-mais-de-32-bytes-123456";
    private static final String ISSUER = "API ALUNO ONLINE";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private AlunoService alunoService;

    @Test
    void deveRetornarJwtQuandoLoginForValido() throws Exception {
        Usuario usuario = usuarioAutenticado();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(UsernamePasswordAuthenticationToken.authenticated(
                        usuario, null, usuario.getAuthorities()));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"aluno@email.com","senha":"senha123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void deveRetornar401QuandoLoginForInvalido() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"login":"aluno@email.com","senha":"senha123"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoAcessarRotaProtegidaSemToken() throws Exception {
        mockMvc.perform(get("/alunos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirAcessoComJwtValido() throws Exception {
        Usuario usuario = usuarioAutenticado();
        String token = tokenService.gerarToken(usuario);
        when(alunoService.listarTodosAlunos()).thenReturn(List.of());

        mockMvc.perform(get("/alunos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar401QuandoJwtTiverAssinaturaAdulterada() throws Exception {
        String token = tokenService.gerarToken(usuarioAutenticado());
        String assinatura = token.substring(token.lastIndexOf('.') + 1);
        String assinaturaAdulterada = assinatura.endsWith("A") ? "B" : "A";
        String tokenAdulterado = token.substring(0, token.lastIndexOf('.') + 1)
                + assinaturaAdulterada;

        mockMvc.perform(get("/alunos")
                        .header("Authorization", "Bearer " + tokenAdulterado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoJwtEstiverExpirado() throws Exception {
        String tokenExpirado = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(LOGIN)
                .withExpiresAt(Date.from(Instant.now().minusSeconds(60)))
                .sign(Algorithm.HMAC256(SECRET));

        mockMvc.perform(get("/alunos")
                        .header("Authorization", "Bearer " + tokenExpirado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveManterHealthPublico() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    private Usuario usuarioAutenticado() {
        Usuario usuario = new Usuario();
        usuario.setLogin(LOGIN);
        usuario.setSenha("hash-de-teste");
        return usuario;
    }
}
