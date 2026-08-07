package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.RegionRequestDTO;
import ma.lpee.lpeebackend.dto.response.RegionResponseDTO;
import ma.lpee.lpeebackend.entity.Region;
import ma.lpee.lpeebackend.mapper.RegionMapper;
import ma.lpee.lpeebackend.repository.RegionRepository;
import ma.lpee.lpeebackend.service.RegionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    @Override
    public RegionResponseDTO create(RegionRequestDTO dto) {

        Region entity = regionMapper.toEntity(dto);

        Region savedEntity = regionRepository.save(entity);

        return regionMapper.toResponseDTO(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public RegionResponseDTO findById(Long id) {

        Region entity = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Région introuvable avec l'id : " + id
                ));

        return regionMapper.toResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegionResponseDTO> findAll() {

        return regionRepository.findAll()
                .stream()
                .map(regionMapper::toResponseDTO)
                .toList();
    }

    @Override
    public RegionResponseDTO update(Long id, RegionRequestDTO dto) {

        Region entity = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Région introuvable avec l'id : " + id
                ));

        regionMapper.updateEntityFromDto(dto, entity);

        Region updatedEntity = regionRepository.save(entity);

        return regionMapper.toResponseDTO(updatedEntity);
    }

    @Override
    public void delete(Long id) {

        Region entity = regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Région introuvable avec l'id : " + id
                ));

        regionRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public RegionResponseDTO findByCodeRegion(String codeRegion) {

        Region entity = regionRepository.findByCodeRegion(codeRegion)
                .orElseThrow(() -> new RuntimeException(
                        "Région introuvable avec le code : " + codeRegion
                ));

        return regionMapper.toResponseDTO(entity);
    }
}