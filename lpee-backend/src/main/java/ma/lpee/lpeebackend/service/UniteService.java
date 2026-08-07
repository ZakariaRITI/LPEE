package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.UniteRequestDTO;
import ma.lpee.lpeebackend.dto.response.UniteResponseDTO;

import java.util.List;

public interface UniteService {

    UniteResponseDTO create(UniteRequestDTO dto);

    UniteResponseDTO getById(Long id);

    List<UniteResponseDTO> getAll();

    UniteResponseDTO update(Long id, UniteRequestDTO dto);

    void delete(Long id);

    List<UniteResponseDTO> getByRegion(Long idRegion);

    UniteResponseDTO getByCodeUnite(String codeUnite);
}

