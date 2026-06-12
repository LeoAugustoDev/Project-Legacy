package br.com.legacy.memoria.application;

import br.com.legacy.handler.APIException;
import br.com.legacy.memoria.api.MemoriaResponse;
import br.com.legacy.memoria.domain.MemoriaContexto;
import br.com.legacy.memoria.infra.MemoriaContextoRepository;
import br.com.legacy.usuario.domain.Usuario;
import br.com.legacy.usuario.infra.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoriaService {

    private final UsuarioRepository usuarioRepository;
    private final MemoriaContextoRepository memoriaContextoRepository;

    @Transactional(readOnly = true)
    public MemoriaResponse buscaMemoriaLogada(String email) {
        Usuario usuario = buscaUsuarioPorEmail(email);

        MemoriaContexto memoria = memoriaContextoRepository.findByUsuario(usuario)
                .orElseThrow(() -> APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Memória de contexto não encontrada."
                ));

        return new MemoriaResponse(memoria);
    }

    private Usuario buscaUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));
    }
}
