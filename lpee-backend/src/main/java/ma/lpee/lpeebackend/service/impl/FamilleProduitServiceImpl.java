package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.FamilleProduitRequestDTO;
import ma.lpee.lpeebackend.dto.response.FamilleProduitResponseDTO;
import ma.lpee.lpeebackend.entity.FamilleProduit;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.FamilleProduitMapper;
import ma.lpee.lpeebackend.repository.FamilleProduitRepository;
import ma.lpee.lpeebackend.service.FamilleProduitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FamilleProduitServiceImpl implements FamilleProduitService {

    private final FamilleProduitRepository familleProduitRepository;
    private final FamilleProduitMapper familleProduitMapper;

    @Override
    public FamilleProduitResponseDTO create(FamilleProduitRequestDTO requestDTO) {

        if (familleProduitRepository.existsByCodeFamille(requestDTO.getCodeFamille())) {
            throw new DuplicateResourceException(
                    "Une famille avec ce code existe déjà."
            );
        }

        FamilleProduit familleProduit = familleProduitMapper.toEntity(requestDTO);

        FamilleProduit saved = familleProduitRepository.save(familleProduit);

        return familleProduitMapper.toResponseDTO(saved);
    }

    @Override
    public FamilleProduitResponseDTO update(Long id, FamilleProduitRequestDTO requestDTO) {

        FamilleProduit familleProduit = familleProduitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Famille produit introuvable."
                ));

        if (!familleProduit.getCodeFamille().equals(requestDTO.getCodeFamille())
                && familleProduitRepository.existsByCodeFamille(requestDTO.getCodeFamille())) {
            throw new DuplicateResourceException(
                    "Une famille avec ce code existe déjà."
            );
        }

        familleProduitMapper.updateEntityFromDto(requestDTO, familleProduit);

        FamilleProduit updated = familleProduitRepository.save(familleProduit);

        return familleProduitMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public FamilleProduitResponseDTO getById(Long id) {

        FamilleProduit familleProduit = familleProduitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Famille produit introuvable."
                ));

        return familleProduitMapper.toResponseDTO(familleProduit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamilleProduitResponseDTO> getAll() {

        return familleProduitRepository.findAll()
                .stream()
                .map(familleProduitMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        FamilleProduit familleProduit = familleProduitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Famille produit introuvable."
                ));

        familleProduitRepository.delete(familleProduit);
    }
}