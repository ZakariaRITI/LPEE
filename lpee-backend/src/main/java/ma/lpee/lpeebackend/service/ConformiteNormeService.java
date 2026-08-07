package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.ConformiteNormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.ConformiteNormeResponseDTO;

import java.util.List;

public interface ConformiteNormeService {

    ConformiteNormeResponseDTO create(ConformiteNormeRequestDTO requestDTO);

    ConformiteNormeResponseDTO update(Long id, ConformiteNormeRequestDTO requestDTO);

    ConformiteNormeResponseDTO getById(Long id);

    List<ConformiteNormeResponseDTO> getAll();

    List<ConformiteNormeResponseDTO> getByEssai(Long idEssai);

    List<ConformiteNormeResponseDTO> getByNorme(Long idNorme);

    void delete(Long id);
}