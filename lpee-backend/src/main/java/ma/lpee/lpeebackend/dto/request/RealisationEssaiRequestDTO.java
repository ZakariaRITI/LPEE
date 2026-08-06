package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RealisationEssaiRequestDTO {
    @NotNull(message = "L'ID de l'unité est obligatoire")
    private Long idUnite;

    @NotNull(message = "L'ID de l'essai est obligatoire")
    private Long idEssai;

    private LocalDate dateRealisation;
    private String statut;

}