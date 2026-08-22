package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "essai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Essai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_essai")
    private Long idEssai;

    @Column(name = "numero_essai", nullable = false, unique = true, length = 100)
    private String numeroEssai;

    @Column(name = "libelle", nullable = false, length = 200)
    private String libelle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "date_essai")
    private LocalDate dateEssai;

    @Column(name = "etalonnage")
    private Boolean etalonnage;

    @Column(name = "statut", length = 50)
    private String statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produit", nullable = false)
    private Produit produit;
}
