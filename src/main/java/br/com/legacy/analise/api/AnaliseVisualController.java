package br.com.legacy.analise.api;

import br.com.legacy.analise.application.AnaliseVisualService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/analises-visuais")
@RequiredArgsConstructor
public class AnaliseVisualController {

    private final AnaliseVisualService analiseVisualService;

    @GetMapping
    public List<AnaliseVisualResponse> listaAnalises(Authentication authentication) {
        return analiseVisualService.listaAnalises(authentication.getName());
    }

    @GetMapping("/{idAnalise}")
    public AnaliseVisualResponse buscaAnalise(
            Authentication authentication,
            @PathVariable UUID idAnalise
    ) {
        return analiseVisualService.buscaAnalise(authentication.getName(), idAnalise);
    }
}
