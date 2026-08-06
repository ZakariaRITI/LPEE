package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TypeDocumentRequestDTO {
    @NotBlank(message = "Le nom du type est obligatoire")
    private String nomType;
}