package br.com.legacy.chat.api;

import br.com.legacy.chat.domain.AutorMensagem;
import br.com.legacy.chat.domain.Mensagem;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class MensagemResponse {

    UUID idMensagem;
    AutorMensagem autor;
    String conteudo;
    LocalDateTime criadoEm;

    public MensagemResponse(Mensagem mensagem) {
        this.idMensagem = mensagem.getIdMensagem();
        this.autor = mensagem.getAutor();
        this.conteudo = mensagem.getConteudo();
        this.criadoEm = mensagem.getCriadoEm();
    }
}
