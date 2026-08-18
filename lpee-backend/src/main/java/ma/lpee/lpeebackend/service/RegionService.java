package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.RegionRequestDTO;
import ma.lpee.lpeebackend.dto.response.RegionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RegionService {

    RegionResponseDTO create(RegionRequestDTO dto);

    RegionResponseDTO findById(Long id);

    List<RegionResponseDTO> findAll();

    Page<RegionResponseDTO> findPage(Pageable pageable, String codeRegion);

    RegionResponseDTO update(Long id, RegionRequestDTO dto);

    void delete(Long id);

    RegionResponseDTO findByCodeRegion(String codeRegion);
}
