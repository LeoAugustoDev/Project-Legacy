package br.com.legacy.usuario.api;

import br.com.legacy.usuario.domain.Usuario;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class CadastroUsuarioResponse {

    UUID idUsuario;
    String nome;
    String email;
    LocalDateTime dataCadastro;

    public CadastroUsuarioResponse(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nome = usuario.getNome();
        this.dataCadastro = usuario.getDataCadastro();
        this.email = usuario.getEmail();
    }
}
