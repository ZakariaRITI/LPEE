package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.FamilleProduitRequestDTO;
import ma.lpee.lpeebackend.dto.response.FamilleProduitResponseDTO;

import java.util.List;

public interface FamilleProduitService {

    FamilleProduitResponseDTO create(FamilleProduitRequestDTO requestDTO);

    FamilleProduitResponseDTO update(Long id, FamilleProduitRequestDTO requestDTO);

    FamilleProduitResponseDTO getById(Long id);

    List<FamilleProduitResponseDTO> getAll();

    void delete(Long id);
}