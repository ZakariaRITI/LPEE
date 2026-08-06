package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "type_document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type")
    private Long idType;

    @Column(name = "nom_type", nullable = false, unique = true, length = 150)
    private String nomType;
}