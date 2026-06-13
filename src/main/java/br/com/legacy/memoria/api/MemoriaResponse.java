package br.com.legacy.memoria.api;

import br.com.legacy.memoria.domain.MemoriaContexto;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class MemoriaResponse {

    UUID idMemoria;
    UUID idUsuario;
    String resumo;
    LocalDateTime atualizadoEm;

    public MemoriaResponse(MemoriaContexto memoria) {
        this.idMemoria = memoria.getIdMemoria();
        this.idUsuario = memoria.getUsuario().getIdUsuario();
        this.resumo = memoria.getResumo();
        this.atualizadoEm = memoria.getAtualizadoEm();
    }
}
