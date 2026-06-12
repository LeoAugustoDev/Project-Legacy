package br.com.legacy.perfil.api;

import br.com.legacy.perfil.application.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    @GetMapping("/me")
    public PerfilResponse buscaPerfilLogado(Authentication authentication) {
        return perfilService.buscaPerfilLogado(authentication.getName());
    }

    @PutMapping("/me")
    public PerfilResponse criaOuAtualizaPerfilLogado(
            Authentication authentication,
            @Valid @RequestBody PerfilRequest request
    ) {
        return perfilService.criaOuAtualizaPerfilLogado(authentication.getName(), request);
    }
}
