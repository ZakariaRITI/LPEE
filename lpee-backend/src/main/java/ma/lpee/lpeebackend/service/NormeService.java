package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.NormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.NormeResponseDTO;

import java.util.List;

public interface NormeService {

    NormeResponseDTO create(NormeRequestDTO requestDTO);

    NormeResponseDTO update(Long id, NormeRequestDTO requestDTO);

    NormeResponseDTO getById(Long id);

    List<NormeResponseDTO> getAll();

    void delete(Long id);
}