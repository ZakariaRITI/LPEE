package ma.lpee.lpeebackend.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PublicationNormeResponseDTO {
    private Long idPublication;
    private Long idNorme;
    private Long idOrganisme;
    private LocalDate datePublication;
    private String statut;
    private LocalDateTime creeLe;
    private Long creePar;
    private LocalDateTime modifieLe;
    private Long modifiePar;
    private LocalDateTime annuleLe;
    private Long annulePar;
}