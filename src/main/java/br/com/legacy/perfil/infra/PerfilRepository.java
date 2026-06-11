package br.com.legacy.perfil.infra;

import br.com.legacy.perfil.domain.Perfil;
import br.com.legacy.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PerfilRepository extends JpaRepository<Perfil, UUID> {

    Optional<Perfil> findByUsuario(Usuario usuario);
}
