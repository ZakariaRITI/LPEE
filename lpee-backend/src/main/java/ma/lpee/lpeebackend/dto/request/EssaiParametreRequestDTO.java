package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class EssaiParametreRequestDTO {
    @NotNull(message = "L'ID de l'essai est obligatoire")
    private Long idEssai;

    @NotNull(message = "L'ID du paramètre est obligatoire")
    private Long idParametre;

    private BigDecimal valeurCible;
    private String statut;

}