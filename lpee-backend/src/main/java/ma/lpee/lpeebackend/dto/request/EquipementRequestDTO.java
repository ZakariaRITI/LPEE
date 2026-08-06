package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EquipementRequestDTO {
    @NotNull(message = "L'ID de la marque est obligatoire")
    private Long idMarque;

    @NotBlank(message = "Le numéro de série est obligatoire")
    private String numeroSerie;

    @NotBlank(message = "La désignation est obligatoire")
    private String designation;

    private String modele;
    private Boolean etalonnageRequis;
    private String periodiciteEtalonnage;
    private String statut;
}