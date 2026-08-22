package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "norme")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Norme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_norme")
    private Long idNorme;

    @Column(name = "numero_norme", nullable = false, unique = true, length = 100)
    private String numeroNorme;

    @Column(name = "code_norme", nullable = false, unique = true, length = 50)
    private String codeNorme;

    @Column(name = "nom_norme", nullable = false, length = 150)
    private String nomNorme;

    @Column(name = "annee")
    private Integer annee;

    @Column(name = "statut", length = 50)
    private String statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organisme")
    private Organisme organisme;

    @ManyToMany(mappedBy = "normes", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Document> documents = new HashSet<>();
}
