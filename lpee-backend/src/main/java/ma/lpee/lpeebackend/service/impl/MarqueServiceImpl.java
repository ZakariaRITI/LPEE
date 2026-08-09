package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.MarqueRequestDTO;
import ma.lpee.lpeebackend.dto.response.MarqueResponseDTO;
import ma.lpee.lpeebackend.entity.Marque;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.MarqueMapper;
import ma.lpee.lpeebackend.repository.MarqueRepository;
import ma.lpee.lpeebackend.service.MarqueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MarqueServiceImpl implements MarqueService {

    private final MarqueRepository marqueRepository;
    private final MarqueMapper marqueMapper;

    @Override
    public MarqueResponseDTO create(MarqueRequestDTO requestDTO) {

        if (marqueRepository.existsByNomMarque(requestDTO.getNomMarque())) {
            throw new DuplicateResourceException(
                    "Une marque avec ce nom existe déjà."
            );
        }

        Marque marque = marqueMapper.toEntity(requestDTO);

        Marque saved = marqueRepository.save(marque);

        return marqueMapper.toResponseDTO(saved);
    }

    @Override
    public MarqueResponseDTO update(Long id, MarqueRequestDTO requestDTO) {

        Marque marque = marqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Marque introuvable."
                ));

        if (!marque.getNomMarque().equals(requestDTO.getNomMarque())
                && marqueRepository.existsByNomMarque(requestDTO.getNomMarque())) {
            throw new DuplicateResourceException(
                    "Une marque avec ce nom existe déjà."
            );
        }

        marqueMapper.updateEntityFromDto(requestDTO, marque);

        Marque updated = marqueRepository.save(marque);

        return marqueMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public MarqueResponseDTO getById(Long id) {

        Marque marque = marqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Marque introuvable."
                ));

        return marqueMapper.toResponseDTO(marque);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarqueResponseDTO> getAll() {

        return marqueRepository.findAll()
                .stream()
                .map(marqueMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Marque marque = marqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Marque introuvable."
                ));

        marqueRepository.delete(marque);
    }
}