package br.com.legacy.analise.infra;

import br.com.legacy.analise.domain.AnaliseVisual;
import br.com.legacy.imagem.domain.ImagemAnalise;
import br.com.legacy.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnaliseVisualRepository extends JpaRepository<AnaliseVisual, UUID> {

    List<AnaliseVisual> findByUsuarioOrderByCriadoEmDesc(Usuario usuario);

    List<AnaliseVisual> findByImagemAnaliseAndUsuarioOrderByCriadoEmDesc(ImagemAnalise imagemAnalise, Usuario usuario);

    Optional<AnaliseVisual> findByIdAnaliseAndUsuario(UUID idAnalise, Usuario usuario);
}
