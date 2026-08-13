package ma.lpee.lpeebackend.security;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.repository.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {
    private final UtilisateurRepository utilisateurRepository;

    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Utilisateur authentifie introuvable.");
        }
        return utilisateurRepository.findByMatricule(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur authentifie introuvable."))
                .getIdUser();
    }
}
