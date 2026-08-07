package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.RegionRequestDTO;
import ma.lpee.lpeebackend.dto.response.RegionResponseDTO;

import java.util.List;

public interface RegionService {

    RegionResponseDTO create(RegionRequestDTO dto);

    RegionResponseDTO findById(Long id);

    List<RegionResponseDTO> findAll();

    RegionResponseDTO update(Long id, RegionRequestDTO dto);

    void delete(Long id);

    RegionResponseDTO findByCodeRegion(String codeRegion);
}