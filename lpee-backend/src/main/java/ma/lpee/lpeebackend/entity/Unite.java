package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "unite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unite")
    private Long idUnite;

    @Column(name = "code_unite", nullable = false, unique = true, length = 50)
    private String codeUnite;

    @Column(name = "nom_unite", nullable = false, unique = true, length = 150)
    private String nomUnite;

    @Column(name = "type_unite", length = 100)
    private String typeUnite;

    @Column(name = "ville", length = 100)
    private String ville;

    @Column(name = "adresse", length = 255)
    private String adresse;

    @Column(name = "telephone", length = 50)
    private String telephone;

    @Column(name = "nbr_operateur_saisie")
    private Integer nbrOperateurSaisie;

    @Column(name = "nbr_responsable_dossier")
    private Integer nbrResponsableDossier;

    @Column(name = "nbr_responsable_laboratoire")
    private Integer nbrResponsableLaboratoire;

    @Column(name = "nbr_responsable_chantier")
    private Integer nbrResponsableChantier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_region", nullable = false)
    private Region region;
}