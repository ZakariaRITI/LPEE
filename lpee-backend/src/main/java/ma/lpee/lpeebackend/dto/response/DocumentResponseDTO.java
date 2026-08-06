package ma.lpee.lpeebackend.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DocumentResponseDTO {
    private Long idDocument;
    private Long idType;
    private String numeroDocument;
    private String nomDocument;
    private String urlDocument;
    private Integer version;
    private LocalDate dateDocument;
}