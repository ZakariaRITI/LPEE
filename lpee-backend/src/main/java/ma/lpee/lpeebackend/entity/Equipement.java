package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "equipement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipement")
    private Long idEquipement;

    @Column(name = "numero_serie", nullable = false, unique = true, length = 100)
    private String numeroSerie;

    @Column(name = "designation", nullable = false, length = 150)
    private String designation;

    @Column(name = "modele", length = 100)
    private String modele;

    @Column(name = "etalonnage_requis")
    private Boolean etalonnageRequis;

    @Column(name = "periodicite_etalonnage", length = 50)
    private String periodiciteEtalonnage;

    @Column(name = "statut", length = 50)
    private String statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marque", nullable = false)
    private Marque marque;
}