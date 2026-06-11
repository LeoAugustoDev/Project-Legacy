package br.com.legacy.usuario.api;

import br.com.legacy.usuario.application.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CadastroUsuarioResponse cadastraUsuario(
            @Valid @RequestBody CadastroUsuarioRequest request) {
        return usuarioService.cadastroUsuario(request);
    }

    @GetMapping("/me")
    public CadastroUsuarioResponse buscaUsuarioLogado(Authentication authentication) {
        return usuarioService.buscaUsuarioLogado(authentication.getName());
    }
}
