package br.com.legacy.memoria.infra;

import br.com.legacy.memoria.domain.MemoriaContexto;
import br.com.legacy.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemoriaContextoRepository extends JpaRepository<MemoriaContexto, UUID> {

    Optional<MemoriaContexto> findByUsuario(Usuario usuario);
}
