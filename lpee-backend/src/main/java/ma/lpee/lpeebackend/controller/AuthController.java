package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.auth.LoginRequestDTO;
import ma.lpee.lpeebackend.dto.response.auth.LoginResponseDTO;
import ma.lpee.lpeebackend.entity.Utilisateur;
import ma.lpee.lpeebackend.repository.UtilisateurRepository;
import ma.lpee.lpeebackend.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getMatricule(),
                                dto.getMotDePasse()
                        )
                );

        Utilisateur utilisateur =
                utilisateurRepository.findByMatricule(dto.getMatricule())
                        .orElseThrow();

        String token = jwtService.generateToken(
                utilisateur.getMatricule(),
                utilisateur.getRole().getCodeRole()
        );

        LoginResponseDTO response =
                new LoginResponseDTO(
                        token,
                        "Bearer",
                        utilisateur.getIdUser(),
                        utilisateur.getMatricule(),
                        utilisateur.getEmail(),
                        utilisateur.getRole().getCodeRole()
                );

        return ResponseEntity.ok(response);
    }
}
