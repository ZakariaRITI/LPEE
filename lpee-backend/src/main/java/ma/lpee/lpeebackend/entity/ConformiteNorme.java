package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "conformite_norme",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_essai", "id_norme"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConformiteNorme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conformite")
    private Long idConformite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_essai", nullable = false)
    private Essai essai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_norme", nullable = false)
    private Norme norme;

    @Column(name = "statut_conformite", length = 50)
    private String statutConformite;

    @Column(name = "date_evaluation")
    private LocalDate dateEvaluation;

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