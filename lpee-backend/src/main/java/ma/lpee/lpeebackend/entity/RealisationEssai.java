package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "realisation_essai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealisationEssai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_realisation")
    private Long idRealisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unite", nullable = false)
    private Unite unite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_essai", nullable = false)
    private Essai essai;

    @Column(name = "date_realisation")
    private LocalDate dateRealisation;

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