package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "marque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_marque")
    private Long idMarque;

    @Column(name = "nom_marque", nullable = false, unique = true, length = 150)
    private String nomMarque;

    @Column(name = "nom_fabricant", length = 150)
    private String nomFabricant;
}