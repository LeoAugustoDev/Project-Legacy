package br.com.legacy.chat.api;

import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class ConversaRequest {

    @Size(max = 100)
    String titulo;
}
