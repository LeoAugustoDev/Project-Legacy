package br.com.legacy.dashboard.application;

import br.com.legacy.dashboard.api.DashboardResponse;
import br.com.legacy.handler.APIException;
import br.com.legacy.perfil.domain.Perfil;
import br.com.legacy.perfil.infra.PerfilRepository;
import br.com.legacy.usuario.domain.Usuario;
import br.com.legacy.usuario.infra.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;

    @Transactional(readOnly = true)
    public DashboardResponse buscaDashboard(String email) {
        Usuario usuario = buscaUsuarioPorEmail(email);
        Optional<Perfil> perfil = perfilRepository.findByUsuario(usuario);

        return perfil
                .map(perfilEncontrado -> dashboardComPerfil(usuario, perfilEncontrado))
                .orElseGet(() -> dashboardSemPerfil(usuario));
    }

    private DashboardResponse dashboardComPerfil(Usuario usuario, Perfil perfil) {
        return new DashboardResponse(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                true,
                perfil.getProfissao(),
                perfil.getObjetivoImagem(),
                "Perfil preenchido",
                List.of(
                        "Iniciar conversa com consultor LEGACY",
                        "Enviar foto para análise visual"
                )
        );
    }

    private DashboardResponse dashboardSemPerfil(Usuario usuario) {
        return new DashboardResponse(
                usuario.getIdUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                false,
                null,
                null,
                "Perfil pendente",
                List.of(
                        "Preencher perfil de imagem",
                        "Depois iniciar conversa com consultor LEGACY"
                )
        );
    }

    private Usuario buscaUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));
    }
}
