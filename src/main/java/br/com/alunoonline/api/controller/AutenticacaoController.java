package br.com.alunoonline.api.controller;

import br.com.alunoonline.api.dtos.DadosAutenticacao;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    private final AuthenticationManager manager;

    public AutenticacaoController(AuthenticationManager manager) {
        this.manager = manager;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        var token = new UsernamePasswordAuthenticationToken(
                dados.login(),
                dados.senha()
        );

        manager.authenticate(token);

        return ResponseEntity.ok().build();
    }
}
