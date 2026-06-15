package br.com.legacy.imagem.api;

import br.com.legacy.imagem.domain.ImagemAnalise;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class ImagemResponse {

    UUID idImagem;
    String nomeArquivo;
    String urlArquivo;
    String contentType;
    Long tamanho;
    LocalDateTime criadoEm;

    public ImagemResponse(ImagemAnalise imagem) {
        this.idImagem = imagem.getIdImagem();
        this.nomeArquivo = imagem.getNomeArquivo();
        this.urlArquivo = "/imagens/" + imagem.getIdImagem() + "/arquivo";
        this.contentType = imagem.getContentType();
        this.tamanho = imagem.getTamanho();
        this.criadoEm = imagem.getCriadoEm();
    }
}
