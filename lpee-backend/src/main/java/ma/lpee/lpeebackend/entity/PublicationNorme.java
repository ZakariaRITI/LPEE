package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "publication_norme",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_norme", "id_organisme"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicationNorme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publication")
    private Long idPublication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_norme", nullable = false)
    private Norme norme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organisme", nullable = false)
    private Organisme organisme;

    @Column(name = "date_publication")
    private LocalDate datePublication;

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