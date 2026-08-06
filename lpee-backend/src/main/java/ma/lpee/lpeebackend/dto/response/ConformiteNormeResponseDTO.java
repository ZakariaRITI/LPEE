package ma.lpee.lpeebackend.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ConformiteNormeResponseDTO {
    private Long idConformite;
    private Long idEssai;
    private Long idNorme;
    private String statutConformite;
    private LocalDate dateEvaluation;
    private String statut;
    private LocalDateTime creeLe;
    private Long creePar;
    private LocalDateTime modifieLe;
    private Long modifiePar;
    private LocalDateTime annuleLe;
    private Long annulePar;
}