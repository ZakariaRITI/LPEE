package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.UtilisateurRequestDTO;
import ma.lpee.lpeebackend.dto.response.UtilisateurResponseDTO;

import java.util.List;

public interface UtilisateurService {

    UtilisateurResponseDTO create(UtilisateurRequestDTO dto);

    UtilisateurResponseDTO getById(Long id);

    List<UtilisateurResponseDTO> getAll();

    UtilisateurResponseDTO update(Long id, UtilisateurRequestDTO dto);

    void delete(Long id);

    List<UtilisateurResponseDTO> getByUnite(Long idUnite);

    List<UtilisateurResponseDTO> getByRole(Long idRole);

    UtilisateurResponseDTO getByEmail(String email);
}

