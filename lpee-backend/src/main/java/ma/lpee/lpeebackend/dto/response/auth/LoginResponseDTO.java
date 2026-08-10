package ma.lpee.lpeebackend.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String type;
    private Long idUser;
    private String email;
    private String role;
}