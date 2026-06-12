package br.com.legacy.chat.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class ChatRequest {

    @NotBlank
    @Size(max = 1000)
    String mensagem;

}
