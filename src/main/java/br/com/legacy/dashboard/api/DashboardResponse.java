package br.com.legacy.dashboard.api;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class DashboardResponse {

    UUID idUsuario;
    String nome;
    String email;
    Boolean perfilPreenchido;
    String profissao;
    String objetivoImagem;
    String statusJornada;
    List<String> proximasRecomendacoes;
}
