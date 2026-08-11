package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.Pattern;

@Data
public class UtilisateurRequestDTO {
    @NotNull(message = "L'ID du rôle est obligatoire")
    private Long idRole;

    @NotNull(message = "L'ID de l'unité est obligatoire")
    private Long idUnite;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String nomUser;

    @NotBlank(message = "Le matricule est obligatoire")
    private String matricule;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial"
    )
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;

    private String statut;
}
