package br.com.legacy.autenticacao.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class LoginRequest {

    @Email
    @NotBlank
    String email;

    @NotBlank
    String senha;
}
