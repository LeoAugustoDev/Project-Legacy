package br.com.legacy.chat.infra;

import br.com.legacy.chat.domain.Conversa;
import br.com.legacy.chat.domain.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MensagemRepository extends JpaRepository<Mensagem, UUID> {

    List<Mensagem> findByConversaOrderByCriadoEmAsc(Conversa conversa);

    List<Mensagem> findTop5ByConversaOrderByCriadoEmDesc(Conversa conversa);
}
