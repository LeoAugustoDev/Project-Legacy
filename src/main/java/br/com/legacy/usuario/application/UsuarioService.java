package br.com.legacy.usuario.application;

import br.com.legacy.handler.APIException;
import br.com.legacy.usuario.api.CadastroUsuarioRequest;
import br.com.legacy.usuario.api.CadastroUsuarioResponse;
import br.com.legacy.usuario.domain.Usuario;
import br.com.legacy.usuario.infra.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class UsuarioService {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public CadastroUsuarioResponse cadastroUsuario(CadastroUsuarioRequest request) {

        usuarioRepository.findByEmail(request.getEmail())
                .ifPresent(usuario -> {
                    log.warn("Tentativa de cadastro com e-mail ja existente");
                    throw APIException.build(
                            HttpStatus.BAD_REQUEST,
                            "E-mail já cadastrado."
                    );
                });

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .dataCadastro(LocalDateTime.now())
                .build();

        usuarioRepository.save(usuario);

        log.info("Usuario cadastrado com sucesso. usuarioId={}", usuario.getIdUsuario());

        return new CadastroUsuarioResponse(usuario);

    }

    public CadastroUsuarioResponse buscaUsuarioLogado(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));

        return new CadastroUsuarioResponse(usuario);
    }
}
