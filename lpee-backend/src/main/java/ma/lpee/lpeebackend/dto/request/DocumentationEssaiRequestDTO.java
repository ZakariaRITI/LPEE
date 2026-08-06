package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentationEssaiRequestDTO {
    @NotNull(message = "L'ID de l'essai est obligatoire")
    private Long idEssai;

    @NotNull(message = "L'ID du document est obligatoire")
    private Long idDocument;

    private String statut;

}