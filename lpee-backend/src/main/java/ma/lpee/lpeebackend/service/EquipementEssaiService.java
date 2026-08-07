package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.EquipementEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.EquipementEssaiResponseDTO;

import java.util.List;

public interface EquipementEssaiService {

    EquipementEssaiResponseDTO create(EquipementEssaiRequestDTO requestDTO);

    EquipementEssaiResponseDTO update(Long id, EquipementEssaiRequestDTO requestDTO);

    EquipementEssaiResponseDTO getById(Long id);

    List<EquipementEssaiResponseDTO> getAll();

    List<EquipementEssaiResponseDTO> getByEssai(Long idEssai);

    List<EquipementEssaiResponseDTO> getByEquipement(Long idEquipement);

    void delete(Long id);
}