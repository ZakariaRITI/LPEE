package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "produit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Long idProduit;

    @Column(name = "code_produit", nullable = false, unique = true, length = 50)
    private String codeProduit;

    @Column(name = "nom_produit", nullable = false, length = 150)
    private String nomProduit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_famille", nullable = false)
    private FamilleProduit familleProduit;
}