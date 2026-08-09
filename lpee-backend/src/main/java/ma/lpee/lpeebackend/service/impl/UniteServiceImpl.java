package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.UniteRequestDTO;
import ma.lpee.lpeebackend.dto.response.UniteResponseDTO;
import ma.lpee.lpeebackend.entity.Unite;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.UniteMapper;
import ma.lpee.lpeebackend.repository.RegionRepository;
import ma.lpee.lpeebackend.repository.UniteRepository;
import ma.lpee.lpeebackend.service.UniteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UniteServiceImpl implements UniteService {

    private final UniteRepository uniteRepository;
    private final RegionRepository regionRepository;
    private final UniteMapper uniteMapper;

    @Override
    public UniteResponseDTO create(UniteRequestDTO dto) {

        if (uniteRepository.existsByCodeUnite(dto.getCodeUnite())) {
            throw new DuplicateResourceException(
                    "Une unité avec le code '" + dto.getCodeUnite() + "' existe déjà"
            );
        }

        if (!regionRepository.existsById(dto.getIdRegion())) {
            throw new ResourceNotFoundException(
                    "La région avec l'ID " + dto.getIdRegion() + " n'existe pas"
            );
        }

        Unite entity = uniteMapper.toEntity(dto);

        Unite saved = uniteRepository.save(entity);

        return uniteMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UniteResponseDTO getById(Long id) {

        Unite entity = uniteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Unité introuvable avec l'ID " + id
                ));

        return uniteMapper.toResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UniteResponseDTO> getAll() {

        return uniteRepository.findAll()
                .stream()
                .map(uniteMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UniteResponseDTO update(Long id, UniteRequestDTO dto) {

        Unite entity = uniteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Unité introuvable avec l'ID " + id
                ));

        if (!entity.getCodeUnite().equals(dto.getCodeUnite())
                && uniteRepository.existsByCodeUnite(dto.getCodeUnite())) {

            throw new DuplicateResourceException(
                    "Une unité avec le code '" + dto.getCodeUnite() + "' existe déjà"
            );
        }

        if (!regionRepository.existsById(dto.getIdRegion())) {
            throw new ResourceNotFoundException(
                    "La région avec l'ID " + dto.getIdRegion() + " n'existe pas"
            );
        }

        uniteMapper.updateEntityFromDto(dto, entity);

        Unite updated = uniteRepository.save(entity);

        return uniteMapper.toResponseDTO(updated);
    }

    @Override
    public void delete(Long id) {

        if (!uniteRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Unité introuvable avec l'ID " + id
            );
        }

        uniteRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UniteResponseDTO> getByRegion(Long idRegion) {

        if (!regionRepository.existsById(idRegion)) {
            throw new ResourceNotFoundException(
                    "La région avec l'ID " + idRegion + " n'existe pas"
            );
        }

        return uniteRepository.findByRegionIdRegion(idRegion)
                .stream()
                .map(uniteMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UniteResponseDTO getByCodeUnite(String codeUnite) {

        Unite entity = uniteRepository.findByCodeUnite(codeUnite)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Unité introuvable avec le code '" + codeUnite + "'"
                ));

        return uniteMapper.toResponseDTO(entity);
    }
}