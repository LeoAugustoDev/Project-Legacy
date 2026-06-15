package br.com.legacy.imagem.api;

import lombok.Value;
import org.springframework.core.io.Resource;

@Value
public class ImagemArquivoResponse {

    Resource arquivo;
    String nomeArquivo;
    String contentType;
    Long tamanho;
}
