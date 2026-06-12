package br.com.legacy.perfil.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class PerfilRequest {

    @NotBlank
    String profissao;

    @NotBlank
    String objetivoImagem;

    @NotBlank
    String estiloDesejado;

    @NotBlank
    String contextoUso;

    String coresPreferidas;

    String restricoes;
}
