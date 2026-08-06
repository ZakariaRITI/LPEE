package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NormeRequestDTO {
    @NotBlank(message = "Le numéro de norme est obligatoire")
    private String numeroNorme;

    @NotBlank(message = "Le code norme est obligatoire")
    private String codeNorme;

    @NotBlank(message = "Le nom de la norme est obligatoire")
    private String nomNorme;

    private Integer annee;
    private String statut;
}