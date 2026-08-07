package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.EquipementRequestDTO;
import ma.lpee.lpeebackend.dto.response.EquipementResponseDTO;

import java.util.List;

public interface EquipementService {

    EquipementResponseDTO create(EquipementRequestDTO requestDTO);

    EquipementResponseDTO update(Long id, EquipementRequestDTO requestDTO);

    EquipementResponseDTO getById(Long id);

    List<EquipementResponseDTO> getAll();

    List<EquipementResponseDTO> getByMarque(Long idMarque);

    void delete(Long id);
}