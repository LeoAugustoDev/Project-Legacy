package br.com.legacy.chat.api;

import br.com.legacy.chat.domain.Conversa;
import br.com.legacy.chat.domain.Mensagem;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
public class ConversaResponse {

    UUID idConversa;
    String titulo;
    LocalDateTime criadoEm;
    LocalDateTime atualizadoEm;
    List<MensagemResponse> mensagens;

    public ConversaResponse(Conversa conversa, List<Mensagem> mensagens) {
        this.idConversa = conversa.getIdConversa();
        this.titulo = conversa.getTitulo();
        this.criadoEm = conversa.getCriadoEm();
        this.atualizadoEm = conversa.getAtualizadoEm();
        this.mensagens = mensagens.stream()
                .map(MensagemResponse::new)
                .toList();
    }
}
