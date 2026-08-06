package ma.lpee.lpeebackend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentationEssaiResponseDTO {
    private Long idDocumentationEssai;
    private Long idEssai;
    private Long idDocument;
    private String statut;
    private LocalDateTime creeLe;
    private Long creePar;
    private LocalDateTime modifieLe;
    private Long modifiePar;
    private LocalDateTime annuleLe;
    private Long annulePar;
}