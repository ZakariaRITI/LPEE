package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.EssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.EssaiResponseDTO;

import java.util.List;

public interface EssaiService {

    EssaiResponseDTO create(EssaiRequestDTO requestDTO);

    EssaiResponseDTO update(Long id, EssaiRequestDTO requestDTO);

    EssaiResponseDTO getById(Long id);

    List<EssaiResponseDTO> getAll();

    List<EssaiResponseDTO> getByProduit(Long idProduit);

    List<EssaiResponseDTO> getByStatut(String statut);

    void delete(Long id);
}