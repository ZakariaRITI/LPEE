package ma.lpee.lpeebackend.dto.response;

import lombok.Data;

@Data
public class EquipementResponseDTO {
    private Long idEquipement;
    private Long idMarque;
    private String numeroSerie;
    private String designation;
    private String modele;
    private Boolean etalonnageRequis;
    private String periodiciteEtalonnage;
    private String statut;
}