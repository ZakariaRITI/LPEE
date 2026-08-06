package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegionRequestDTO {
    @NotBlank(message = "Le code région est obligatoire")
    private String codeRegion;

    @NotBlank(message = "Le nom de la région est obligatoire")
    private String nomRegion;
}