package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parametre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parametre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametre")
    private Long idParametre;

    @Column(name = "nom_parametre", nullable = false, unique = true, length = 150)
    private String nomParametre;

    @Column(name = "unite_parametre", length = 50)
    private String uniteParametre;
}