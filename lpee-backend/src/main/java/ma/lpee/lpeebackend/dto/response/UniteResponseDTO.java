package ma.lpee.lpeebackend.dto.response;

import lombok.Data;

@Data
public class UniteResponseDTO {
    private Long idUnite;
    private Long idRegion;
    private String codeUnite;
    private String nomUnite;
    private String typeUnite;
    private String ville;
    private String adresse;
    private String telephone;
    private Integer nbrOperateurSaisie;
    private Integer nbrResponsableDossier;
    private Integer nbrResponsableLaboratoire;
    private Integer nbrResponsableChantier;
}