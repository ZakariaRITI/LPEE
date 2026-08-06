package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "region")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_region")
    private Long idRegion;

    @Column(name = "code_region", nullable = false, unique = true, length = 50)
    private String codeRegion;

    @Column(name = "nom_region", nullable = false, length = 150)
    private String nomRegion;
}