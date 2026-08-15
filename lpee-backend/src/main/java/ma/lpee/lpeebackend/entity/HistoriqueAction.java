package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_action", indexes = {
        @Index(name = "idx_historique_date", columnList = "date_heure"),
        @Index(name = "idx_historique_essai", columnList = "id_essai")
})
@Getter
@Setter
@NoArgsConstructor
public class HistoriqueAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idHistorique;

    @Column(nullable = false, length = 30)
    private String action;

    @Column(nullable = false, length = 80)
    private String elementType;

    private Long elementId;

    @Column(length = 500)
    private String elementLibelle;

    @Column(name = "id_essai")
    private Long idEssai;

    @Column(length = 150)
    private String numeroEssai;

    private Long idUser;

    @Column(nullable = false, length = 100)
    private String matricule;

    @Column(nullable = false, length = 200)
    private String nomUser;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String changements;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;
}
