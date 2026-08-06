    package ma.lpee.lpeebackend.entity;
    
    import jakarta.persistence.*;
    import lombok.*;
    
    @Entity
    @Table(name = "organisme")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Organisme {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_organisme")
        private Long idOrganisme;
    
        @Column(name = "code_organisme", nullable = false, unique = true, length = 50)
        private String codeOrganisme;
    
        @Column(name = "nom_organisme", nullable = false, length = 150)
        private String nomOrganisme;
    
        @Column(name = "image_organisme", length = 254)
        private String imageOrganisme;
    }
