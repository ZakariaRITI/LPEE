package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentation_essai",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_essai", "id_document"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentationEssai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documentation_essai")
    private Long idDocumentationEssai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_essai", nullable = false)
    private Essai essai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_document", nullable = false)
    private Document document;

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