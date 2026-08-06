package ma.lpee.lpeebackend.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RealisationEssaiResponseDTO {
    private Long idRealisation;
    private Long idUnite;
    private Long idEssai;
    private LocalDate dateRealisation;
    private String statut;
    private LocalDateTime creeLe;
    private Long creePar;
    private LocalDateTime modifieLe;
    private Long modifiePar;
    private LocalDateTime annuleLe;
    private Long annulePar;
}