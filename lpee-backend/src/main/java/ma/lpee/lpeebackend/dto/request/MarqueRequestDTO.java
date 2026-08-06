package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarqueRequestDTO {
    @NotBlank(message = "Le nom de la marque est obligatoire")
    private String nomMarque;

    private String nomFabricant;
}