package br.com.legacy.chat.api;

import br.com.legacy.chat.application.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/mensagens")
    public ChatResponse enviaMensagem(
            Authentication authentication,
            @Valid @RequestBody ChatRequest request
    ) {
        return chatService.enviaMensagem(authentication.getName(), request);
    }
}
