package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.MarqueRequestDTO;
import ma.lpee.lpeebackend.dto.response.MarqueResponseDTO;

import java.util.List;

public interface MarqueService {

    MarqueResponseDTO create(MarqueRequestDTO requestDTO);

    MarqueResponseDTO update(Long id, MarqueRequestDTO requestDTO);

    MarqueResponseDTO getById(Long id);

    List<MarqueResponseDTO> getAll();

    void delete(Long id);
}