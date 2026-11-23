package br.com.alunoonline.api.controller;

import br.com.alunoonline.api.dtos.CadastroUsuarioRequestDTO;
import br.com.alunoonline.api.dtos.UsuarioResponseDTO;
import br.com.alunoonline.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO cadastrar(@Valid @RequestBody CadastroUsuarioRequestDTO dados) {
        return usuarioService.cadastrar(dados);
    }
}
