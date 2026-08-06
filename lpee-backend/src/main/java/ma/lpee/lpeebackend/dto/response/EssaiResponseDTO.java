package ma.lpee.lpeebackend.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EssaiResponseDTO {
    private Long idEssai;
    private Long idProduit;
    private String numeroEssai;
    private String description;
    private LocalDate dateEssai;
    private Boolean etalonnage;
    private String statut;
}