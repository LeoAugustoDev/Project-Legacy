package br.com.legacy.chat.infra;

import br.com.legacy.chat.domain.Conversa;
import br.com.legacy.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversaRepository extends JpaRepository<Conversa, UUID> {

    List<Conversa> findByUsuarioOrderByAtualizadoEmDesc(Usuario usuario);

    Optional<Conversa> findByIdConversaAndUsuario(UUID idConversa, Usuario usuario);
}
