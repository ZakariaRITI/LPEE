package ma.lpee.lpeebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private Long idDocument;

    @Column(name = "numero_document", nullable = false, unique = true, length = 100)
    private String numeroDocument;

    @Column(name = "nom_document", nullable = false, length = 150)
    private String nomDocument;

    @Column(name = "url_document", length = 255)
    private String urlDocument;

    @Column(name = "version")
    private Integer version;

    @Column(name = "date_document")
    private LocalDate dateDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type", nullable = false)
    private TypeDocument typeDocument;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "reference_norme",
            joinColumns = @JoinColumn(name = "id_document"),
            inverseJoinColumns = @JoinColumn(name = "id_norme")
    )
    @Builder.Default
    private Set<Norme> normes = new HashSet<>();

    public void addNorme(Norme norme) {
        normes.add(norme);
        norme.getDocuments().add(this);
    }

    public void removeNorme(Norme norme) {
        normes.remove(norme);
        norme.getDocuments().remove(this);
    }
}