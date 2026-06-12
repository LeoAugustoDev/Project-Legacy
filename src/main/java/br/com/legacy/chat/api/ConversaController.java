package br.com.legacy.chat.api;

import br.com.legacy.chat.application.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/conversas")
@RequiredArgsConstructor
public class ConversaController {

    private final ChatService chatService;

    @GetMapping
    public List<ConversaResumoResponse> listaConversas(Authentication authentication) {
        return chatService.listaConversas(authentication.getName());
    }

    @GetMapping("/{idConversa}")
    public ConversaResponse buscaConversa(
            Authentication authentication,
            @PathVariable UUID idConversa
    ) {
        return chatService.buscaConversa(authentication.getName(), idConversa);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversaResponse criaConversa(
            Authentication authentication,
            @Valid @RequestBody(required = false) ConversaRequest request
    ) {
        return chatService.criaConversa(authentication.getName(), request);
    }

    @PostMapping("/{idConversa}/mensagens")
    public ChatResponse enviaMensagem(
            Authentication authentication,
            @PathVariable UUID idConversa,
            @Valid @RequestBody ChatRequest request
    ) {
        return chatService.enviaMensagemNaConversa(authentication.getName(), idConversa, request);
    }
}
