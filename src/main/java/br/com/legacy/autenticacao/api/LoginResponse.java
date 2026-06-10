package br.com.legacy.autenticacao.api;

import lombok.Value;

@Value
public class LoginResponse {

    String token;
    String tipo = "Bearer";
}
