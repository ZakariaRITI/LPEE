package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.RealisationEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.RealisationEssaiResponseDTO;

import java.util.List;

public interface RealisationEssaiService {

    RealisationEssaiResponseDTO create(RealisationEssaiRequestDTO dto);

    RealisationEssaiResponseDTO findById(Long id);

    List<RealisationEssaiResponseDTO> findAll();

    RealisationEssaiResponseDTO update(Long id, RealisationEssaiRequestDTO dto);

    void delete(Long id);

    List<RealisationEssaiResponseDTO> findByEssai(Long idEssai);

    List<RealisationEssaiResponseDTO> findByUnite(Long idUnite);

    List<RealisationEssaiResponseDTO> findByCreePar(Long idUser);

    List<RealisationEssaiResponseDTO> findByStatut(String statut);
}