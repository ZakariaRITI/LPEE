package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "famille_produit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FamilleProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_famille")
    private Long idFamille;

    @Column(name = "code_famille", nullable = false, unique = true, length = 50)
    private String codeFamille;

    @Column(name = "nom_famille", nullable = false, length = 150)
    private String nomFamille;
}