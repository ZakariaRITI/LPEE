package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EquipementEssaiRequestDTO {
    @NotNull(message = "L'ID de l'essai est obligatoire")
    private Long idEssai;

    @NotNull(message = "L'ID de l'équipement est obligatoire")
    private Long idEquipement;

    private LocalDate dateUtilisationDebut;
    private LocalDate dateUtilisationFin;
    private String statut;

}