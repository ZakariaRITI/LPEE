package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ConformiteNormeRequestDTO {
    @NotNull(message = "L'ID de l'essai est obligatoire")
    private Long idEssai;

    @NotNull(message = "L'ID de la norme est obligatoire")
    private Long idNorme;

    private String statutConformite;
    private LocalDate dateEvaluation;
    private String statut;

}