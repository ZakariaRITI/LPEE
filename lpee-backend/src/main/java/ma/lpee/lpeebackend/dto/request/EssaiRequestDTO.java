package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EssaiRequestDTO {
    @NotNull(message = "L'ID du produit est obligatoire")
    private Long idProduit;

    @NotBlank(message = "Le numéro d'essai est obligatoire")
    private String numeroEssai;

    private String description;
    private LocalDate dateEssai;
    private Boolean etalonnage;
    private String statut;
}