package br.com.legacy.usuario.api;

import br.com.legacy.usuario.application.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}
