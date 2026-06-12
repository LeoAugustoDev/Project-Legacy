package br.com.legacy.chat.api;

import br.com.legacy.chat.domain.Conversa;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class ConversaResumoResponse {

    UUID idConversa;
    String titulo;
    LocalDateTime criadoEm;
    LocalDateTime atualizadoEm;

    public ConversaResumoResponse(Conversa conversa) {
        this.idConversa = conversa.getIdConversa();
        this.titulo = conversa.getTitulo();
        this.criadoEm = conversa.getCriadoEm();
        this.atualizadoEm = conversa.getAtualizadoEm();
    }
}
