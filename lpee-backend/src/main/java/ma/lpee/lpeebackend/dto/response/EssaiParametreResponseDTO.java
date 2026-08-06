package ma.lpee.lpeebackend.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EssaiParametreResponseDTO {
    private Long idMesure;
    private Long idEssai;
    private Long idParametre;
    private BigDecimal valeurCible;
    private String statut;
    private LocalDateTime creeLe;
    private Long creePar;
    private LocalDateTime modifieLe;
    private Long modifiePar;
    private LocalDateTime annuleLe;
    private Long annulePar;
}