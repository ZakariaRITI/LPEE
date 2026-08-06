package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "essai_parametre",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_essai", "id_parametre"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EssaiParametre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mesure")
    private Long idMesure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_essai", nullable = false)
    private Essai essai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parametre", nullable = false)
    private Parametre parametre;

    @Column(name = "valeur_cible", precision = 15, scale = 4)
    private BigDecimal valeurCible;

    @Column(name = "statut", length = 50)
    private String statut;

    @Column(name = "cree_le")
    private LocalDateTime creeLe;

    @Column(name = "cree_par")
    private Long creePar;

    @Column(name = "modifie_le")
    private LocalDateTime modifieLe;

    @Column(name = "modifie_par")
    private Long modifiePar;

    @Column(name = "annule_le")
    private LocalDateTime annuleLe;

    @Column(name = "annule_par")
    private Long annulePar;
}