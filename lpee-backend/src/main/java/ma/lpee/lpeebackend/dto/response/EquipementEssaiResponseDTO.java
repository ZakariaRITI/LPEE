package ma.lpee.lpeebackend.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EquipementEssaiResponseDTO {
    private Long idUtilisationEquipement;
    private Long idEssai;
    private Long idEquipement;
    private LocalDate dateUtilisationDebut;
    private LocalDate dateUtilisationFin;
    private String statut;
    private LocalDateTime creeLe;
    private Long creePar;
    private LocalDateTime modifieLe;
    private Long modifiePar;
    private LocalDateTime annuleLe;
    private Long annulePar;
}