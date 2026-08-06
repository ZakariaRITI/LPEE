package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipement_essai",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_essai", "id_equipement"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipementEssai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisation_equipement")
    private Long idUtilisationEquipement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_essai", nullable = false)
    private Essai essai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipement", nullable = false)
    private Equipement equipement;

    @Column(name = "date_utilisation_debut")
    private LocalDate dateUtilisationDebut;

    @Column(name = "date_utilisation_fin")
    private LocalDate dateUtilisationFin;

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