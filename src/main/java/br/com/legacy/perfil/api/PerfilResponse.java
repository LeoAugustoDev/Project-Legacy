package br.com.legacy.perfil.api;

import br.com.legacy.perfil.domain.Perfil;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class PerfilResponse {

    UUID idPerfil;
    UUID idUsuario;
    String profissao;
    String objetivoImagem;
    String estiloDesejado;
    String contextoUso;
    String coresPreferidas;
    String restricoes;
    LocalDateTime criadoEm;
    LocalDateTime atualizadoEm;

    public PerfilResponse(Perfil perfil) {
        this.idPerfil = perfil.getIdPerfil();
        this.idUsuario = perfil.getUsuario().getIdUsuario();
        this.profissao = perfil.getProfissao();
        this.objetivoImagem = perfil.getObjetivoImagem();
        this.estiloDesejado = perfil.getEstiloDesejado();
        this.contextoUso = perfil.getContextoUso();
        this.coresPreferidas = perfil.getCoresPreferidas();
        this.restricoes = perfil.getRestricoes();
        this.criadoEm = perfil.getCriadoEm();
        this.atualizadoEm = perfil.getAtualizadoEm();
    }
}
