package br.com.legacy.memoria.api;

import br.com.legacy.memoria.application.MemoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memoria")
@RequiredArgsConstructor
public class MemoriaController {

    private final MemoriaService memoriaService;

    @GetMapping("/me")
    public MemoriaResponse buscaMemoriaLogada(Authentication authentication) {
        return memoriaService.buscaMemoriaLogada(authentication.getName());
    }
}
