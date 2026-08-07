package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.ParametreRequestDTO;
import ma.lpee.lpeebackend.dto.response.ParametreResponseDTO;

import java.util.List;

public interface ParametreService {

    ParametreResponseDTO create(ParametreRequestDTO requestDTO);

    ParametreResponseDTO update(Long id, ParametreRequestDTO requestDTO);

    ParametreResponseDTO getById(Long id);

    List<ParametreResponseDTO> getAll();

    void delete(Long id);
}