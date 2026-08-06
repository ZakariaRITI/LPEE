package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParametreRequestDTO {
    @NotBlank(message = "Le nom du paramètre est obligatoire")
    private String nomParametre;

    private String uniteParametre;
}