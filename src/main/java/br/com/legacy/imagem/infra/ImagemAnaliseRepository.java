package br.com.legacy.imagem.infra;

import br.com.legacy.imagem.domain.ImagemAnalise;
import br.com.legacy.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImagemAnaliseRepository extends JpaRepository<ImagemAnalise, UUID> {

    List<ImagemAnalise> findByUsuarioOrderByCriadoEmDesc(Usuario usuario);

    Optional<ImagemAnalise> findByIdImagemAndUsuario(UUID idImagem, Usuario usuario);
}
