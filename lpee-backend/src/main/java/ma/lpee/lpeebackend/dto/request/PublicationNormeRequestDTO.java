package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PublicationNormeRequestDTO {
    @NotNull(message = "L'ID de la norme est obligatoire")
    private Long idNorme;

    @NotNull(message = "L'ID de l'organisme est obligatoire")
    private Long idOrganisme;

    private LocalDate datePublication;
    private String statut;

}