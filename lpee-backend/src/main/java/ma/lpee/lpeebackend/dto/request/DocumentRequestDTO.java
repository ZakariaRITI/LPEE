package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DocumentRequestDTO {
    @NotNull(message = "L'ID du type de document est obligatoire")
    private Long idType;

    @NotBlank(message = "Le numéro de document est obligatoire")
    private String numeroDocument;

    @NotBlank(message = "Le nom du document est obligatoire")
    private String nomDocument;

    private String urlDocument;
    private Integer version;
    private LocalDate dateDocument;
}