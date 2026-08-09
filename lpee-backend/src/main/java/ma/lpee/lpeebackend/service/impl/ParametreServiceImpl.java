package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.ParametreRequestDTO;
import ma.lpee.lpeebackend.dto.response.ParametreResponseDTO;
import ma.lpee.lpeebackend.entity.Parametre;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.ParametreMapper;
import ma.lpee.lpeebackend.repository.ParametreRepository;
import ma.lpee.lpeebackend.service.ParametreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParametreServiceImpl implements ParametreService {

    private final ParametreRepository parametreRepository;
    private final ParametreMapper parametreMapper;

    @Override
    public ParametreResponseDTO create(ParametreRequestDTO requestDTO) {

        if (parametreRepository.existsByNomParametre(requestDTO.getNomParametre())) {
            throw new DuplicateResourceException(
                    "Un paramètre avec ce nom existe déjà."
            );
        }

        Parametre parametre = parametreMapper.toEntity(requestDTO);

        Parametre saved = parametreRepository.save(parametre);

        return parametreMapper.toResponseDTO(saved);
    }

    @Override
    public ParametreResponseDTO update(Long id, ParametreRequestDTO requestDTO) {

        Parametre parametre = parametreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paramètre introuvable."
                ));

        if (!parametre.getNomParametre().equals(requestDTO.getNomParametre())
                && parametreRepository.existsByNomParametre(requestDTO.getNomParametre())) {
            throw new DuplicateResourceException(
                    "Un paramètre avec ce nom existe déjà."
            );
        }

        parametreMapper.updateEntityFromDto(requestDTO, parametre);

        Parametre updated = parametreRepository.save(parametre);

        return parametreMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ParametreResponseDTO getById(Long id) {

        Parametre parametre = parametreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paramètre introuvable."
                ));

        return parametreMapper.toResponseDTO(parametre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParametreResponseDTO> getAll() {

        return parametreRepository.findAll()
                .stream()
                .map(parametreMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Parametre parametre = parametreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paramètre introuvable."
                ));

        parametreRepository.delete(parametre);
    }
}