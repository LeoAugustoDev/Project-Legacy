package br.com.legacy.perfil.application;

import br.com.legacy.handler.APIException;
import br.com.legacy.perfil.api.PerfilRequest;
import br.com.legacy.perfil.api.PerfilResponse;
import br.com.legacy.perfil.domain.Perfil;
import br.com.legacy.perfil.infra.PerfilRepository;
import br.com.legacy.usuario.domain.Usuario;
import br.com.legacy.usuario.infra.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class PerfilService {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public PerfilResponse buscaPerfilLogado(String email) {
        Usuario usuario = buscaUsuarioPorEmail(email);

        Perfil perfil = perfilRepository.findByUsuario(usuario)
                .orElseThrow(() -> APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Perfil não encontrado."
                ));

        return new PerfilResponse(perfil);
    }

    @Transactional
    public PerfilResponse criaOuAtualizaPerfilLogado(String email, PerfilRequest request) {
        Usuario usuario = buscaUsuarioPorEmail(email);

        Perfil perfil = perfilRepository.findByUsuario(usuario)
                .map(perfilExistente -> {
                    perfilExistente.atualizarDados(
                            request.getProfissao(),
                            request.getObjetivoImagem(),
                            request.getEstiloDesejado(),
                            request.getContextoUso(),
                            request.getCoresPreferidas(),
                            request.getRestricoes()
                    );
                    return perfilExistente;
                })
                .orElseGet(() -> Perfil.builder()
                        .usuario(usuario)
                        .profissao(request.getProfissao())
                        .objetivoImagem(request.getObjetivoImagem())
                        .estiloDesejado(request.getEstiloDesejado())
                        .contextoUso(request.getContextoUso())
                        .coresPreferidas(request.getCoresPreferidas())
                        .restricoes(request.getRestricoes())
                        .build());

        Perfil perfilSalvo = perfilRepository.save(perfil);

        log.info("Perfil salvo com sucesso. usuarioId={}", usuario.getIdUsuario());

        return new PerfilResponse(perfilSalvo);
    }

    private Usuario buscaUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));
    }
}