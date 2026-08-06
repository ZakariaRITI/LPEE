package ma.lpee.lpeebackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequestDTO {
    @NotBlank(message = "Le code rôle est obligatoire")
    private String codeRole;

    @NotBlank(message = "Le nom du rôle est obligatoire")
    private String nomRole;
}