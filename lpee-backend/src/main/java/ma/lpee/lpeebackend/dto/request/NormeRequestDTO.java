package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NormeRequestDTO {

    @NotBlank(message = "Le numéro de norme est obligatoire")
    private String numeroNorme;

    @NotBlank(message = "Le code norme est obligatoire")
    private String codeNorme;

    @NotBlank(message = "Le nom de la norme est obligatoire")
    private String nomNorme;

    @NotNull(message = "L'ID de l'organisme est obligatoire")
    private Long idOrganisme;

    private Integer annee;
    private String statut;
}