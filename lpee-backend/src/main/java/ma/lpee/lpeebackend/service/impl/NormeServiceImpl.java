package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.NormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.NormeResponseDTO;
import ma.lpee.lpeebackend.entity.Norme;
import ma.lpee.lpeebackend.entity.Organisme;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.NormeMapper;
import ma.lpee.lpeebackend.repository.NormeRepository;
import ma.lpee.lpeebackend.repository.OrganismeRepository;
import ma.lpee.lpeebackend.service.NormeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NormeServiceImpl implements NormeService {

    private final NormeRepository normeRepository;
    private final OrganismeRepository organismeRepository;
    private final NormeMapper normeMapper;

    @Override
    public NormeResponseDTO create(NormeRequestDTO requestDTO) {

        if (normeRepository.findByNumeroNorme(requestDTO.getNumeroNorme()).isPresent()) {
            throw new DuplicateResourceException(
                    "Une norme avec ce numéro existe déjà."
            );
        }

        if (normeRepository.existsByCodeNorme(requestDTO.getCodeNorme())) {
            throw new DuplicateResourceException(
                    "Une norme avec ce code existe déjà."
            );
        }

        Organisme organisme = requestDTO.getIdOrganisme() == null ? null
                : organismeRepository.findById(requestDTO.getIdOrganisme())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Organisme introuvable."
                        ));

        Norme norme = normeMapper.toEntity(requestDTO);

        norme.setOrganisme(organisme);

        Norme saved = normeRepository.save(norme);

        return normeMapper.toResponseDTO(saved);
    }

    @Override
    public NormeResponseDTO update(Long id, NormeRequestDTO requestDTO) {

        Norme norme = normeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Norme introuvable."
                ));

        if (!norme.getNumeroNorme().equals(requestDTO.getNumeroNorme())
                && normeRepository.findByNumeroNorme(requestDTO.getNumeroNorme()).isPresent()) {
            throw new DuplicateResourceException(
                    "Une norme avec ce numéro existe déjà."
            );
        }

        if (!norme.getCodeNorme().equals(requestDTO.getCodeNorme())
                && normeRepository.existsByCodeNorme(requestDTO.getCodeNorme())) {
            throw new DuplicateResourceException(
                    "Une norme avec ce code existe déjà."
            );
        }

        Organisme organisme = requestDTO.getIdOrganisme() == null ? null
                : organismeRepository.findById(requestDTO.getIdOrganisme())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Organisme introuvable."
                        ));

        normeMapper.updateEntityFromDto(requestDTO, norme);

        norme.setOrganisme(organisme);

        Norme updated = normeRepository.save(norme);

        return normeMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public NormeResponseDTO getById(Long id) {

        Norme norme = normeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Norme introuvable."
                ));

        return normeMapper.toResponseDTO(norme);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NormeResponseDTO> getAll() {

        return normeRepository.findAll()
                .stream()
                .map(normeMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Norme norme = normeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Norme introuvable."
                ));

        normeRepository.delete(norme);
    }
}
