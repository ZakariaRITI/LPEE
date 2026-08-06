package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FamilleProduitRequestDTO {
    @NotBlank(message = "Le code famille est obligatoire")
    private String codeFamille;

    @NotBlank(message = "Le nom de la famille est obligatoire")
    private String nomFamille;
}