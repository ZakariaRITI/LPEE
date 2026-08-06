package ma.lpee.lpeebackend.dto.response;

import lombok.Data;

@Data
public class UtilisateurResponseDTO {
    private Long idUser;
    private Long idRole;
    private Long idUnite;
    private String nomUser;
    private String email;
    private String statut;
}