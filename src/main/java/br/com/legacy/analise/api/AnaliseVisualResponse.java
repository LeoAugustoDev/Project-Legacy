package br.com.legacy.analise.api;

import br.com.legacy.analise.domain.AnaliseVisual;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class AnaliseVisualResponse {

    UUID idAnalise;
    UUID idImagem;
    String nomeArquivo;
    String resultado;
    LocalDateTime criadoEm;

    public AnaliseVisualResponse(AnaliseVisual analiseVisual) {
        this.idAnalise = analiseVisual.getIdAnalise();
        this.idImagem = analiseVisual.getImagemAnalise().getIdImagem();
        this.nomeArquivo = analiseVisual.getImagemAnalise().getNomeArquivo();
        this.resultado = analiseVisual.getResultado();
        this.criadoEm = analiseVisual.getCriadoEm();
    }
}
