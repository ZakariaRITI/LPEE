package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrganismeRequestDTO {
    @NotBlank(message = "Le code organisme est obligatoire")
    private String codeOrganisme;

    @NotBlank(message = "Le nom de l'organisme est obligatoire")
    private String nomOrganisme;

    private String imageOrganisme;
}