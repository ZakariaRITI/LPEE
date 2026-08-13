package ma.lpee.lpeebackend.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record HistoriqueActionResponseDTO(
        String action,
        String essaiConcerne,
        String actionSur,
        String detail,
        Long idUser,
        String matricule,
        String nomUser,
        LocalDate date,
        LocalTime heure) {
}
