package ma.lpee.lpeebackend.dto.response;

import lombok.Data;

@Data
public class NormeResponseDTO {
    private Long idNorme;
    private String numeroNorme;
    private String codeNorme;
    private String nomNorme;
    private Integer annee;
    private String statut;
}