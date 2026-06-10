package br.com.legacy.autenticacao.application;

import br.com.legacy.autenticacao.api.LoginRequest;
import br.com.legacy.autenticacao.api.LoginResponse;
import br.com.legacy.configuracao.security.JwtService;
import br.com.legacy.handler.APIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getSenha()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.gerarToken(userDetails);

            log.info("Login realizado com sucesso");

            return new LoginResponse(token);
        } catch (AuthenticationException exception) {
            log.warn("Tentativa de login invalida");
            throw APIException.build(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciais inválidas."
            );
        }
    }
}
