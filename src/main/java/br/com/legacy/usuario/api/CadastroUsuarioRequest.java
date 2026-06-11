package br.com.legacy.usuario.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class CadastroUsuarioRequest {

    @NotBlank
    String nome;

    @Email
    @NotBlank
    String email;

    @NotBlank
    String senha;
}
