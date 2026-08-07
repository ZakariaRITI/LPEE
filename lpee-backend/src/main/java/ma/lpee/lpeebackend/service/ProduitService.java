package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.ProduitRequestDTO;
import ma.lpee.lpeebackend.dto.response.ProduitResponseDTO;

import java.util.List;

public interface ProduitService {

    ProduitResponseDTO create(ProduitRequestDTO requestDTO);

    ProduitResponseDTO update(Long id, ProduitRequestDTO requestDTO);

    ProduitResponseDTO getById(Long id);

    List<ProduitResponseDTO> getAll();

    List<ProduitResponseDTO> getByFamille(Long idFamille);

    void delete(Long id);
}