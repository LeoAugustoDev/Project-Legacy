package br.com.legacy.memoria.domain;

import br.com.legacy.usuario.domain.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "memorias_contexto")
public class MemoriaContexto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idMemoria;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "text")
    private String resumo;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    public void atualizarResumo(String resumo) {
        this.resumo = resumo;
        this.atualizadoEm = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
