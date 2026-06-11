package br.com.legacy.perfil.domain;

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
@Table(name = "perfis")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idPerfil;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false)
    private String profissao;

    @Column(nullable = false)
    private String objetivoImagem;

    @Column(nullable = false)
    private String estiloDesejado;

    @Column(nullable = false)
    private String contextoUso;

    private String coresPreferidas;

    private String restricoes;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime atualizadoEm;

    public void atualizarDados(
            String profissao,
            String objetivoImagem,
            String estiloDesejado,
            String contextoUso,
            String coresPreferidas,
            String restricoes
    ) {
        this.profissao = profissao;
        this.objetivoImagem = objetivoImagem;
        this.estiloDesejado = estiloDesejado;
        this.contextoUso = contextoUso;
        this.coresPreferidas = coresPreferidas;
        this.restricoes = restricoes;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
