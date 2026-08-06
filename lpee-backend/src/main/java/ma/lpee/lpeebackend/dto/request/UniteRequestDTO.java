package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UniteRequestDTO {
    @NotNull(message = "L'ID de la région est obligatoire")
    private Long idRegion;

    @NotBlank(message = "Le code unité est obligatoire")
    private String codeUnite;

    @NotBlank(message = "Le nom de l'unité est obligatoire")
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