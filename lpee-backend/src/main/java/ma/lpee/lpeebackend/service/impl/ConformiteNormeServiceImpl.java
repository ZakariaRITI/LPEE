package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.ConformiteNormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.ConformiteNormeResponseDTO;
import ma.lpee.lpeebackend.entity.ConformiteNorme;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.entity.Norme;
import ma.lpee.lpeebackend.mapper.ConformiteNormeMapper;
import ma.lpee.lpeebackend.repository.ConformiteNormeRepository;
import ma.lpee.lpeebackend.repository.EssaiRepository;
import ma.lpee.lpeebackend.repository.NormeRepository;
import ma.lpee.lpeebackend.service.ConformiteNormeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ConformiteNormeServiceImpl implements ConformiteNormeService {

    private final ConformiteNormeRepository conformiteNormeRepository;
    private final EssaiRepository essaiRepository;
    private final NormeRepository normeRepository;
    private final ConformiteNormeMapper conformiteNormeMapper;

    @Override
    public ConformiteNormeResponseDTO create(ConformiteNormeRequestDTO requestDTO) {

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new RuntimeException("Essai introuvable."));

        Norme norme = normeRepository.findById(requestDTO.getIdNorme())
                .orElseThrow(() -> new RuntimeException("Norme introuvable."));

        if (conformiteNormeRepository.existsByEssaiIdEssaiAndNormeIdNorme(
                requestDTO.getIdEssai(),
                requestDTO.getIdNorme())) {
            throw new RuntimeException("Cette conformité existe déjà.");
        }

        ConformiteNorme conformiteNorme = conformiteNormeMapper.toEntity(requestDTO);

        conformiteNorme.setEssai(essai);
        conformiteNorme.setNorme(norme);
        conformiteNorme.setCreeLe(LocalDateTime.now());

        ConformiteNorme saved = conformiteNormeRepository.save(conformiteNorme);

        return conformiteNormeMapper.toResponseDTO(saved);
    }

    @Override
    public ConformiteNormeResponseDTO update(Long id, ConformiteNormeRequestDTO requestDTO) {

        ConformiteNorme conformiteNorme = conformiteNormeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conformité introuvable."));

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new RuntimeException("Essai introuvable."));

        Norme norme = normeRepository.findById(requestDTO.getIdNorme())
                .orElseThrow(() -> new RuntimeException("Norme introuvable."));

        conformiteNormeMapper.updateEntityFromDto(requestDTO, conformiteNorme);

        conformiteNorme.setEssai(essai);
        conformiteNorme.setNorme(norme);
        conformiteNorme.setModifieLe(LocalDateTime.now());

        ConformiteNorme updated = conformiteNormeRepository.save(conformiteNorme);

        return conformiteNormeMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ConformiteNormeResponseDTO getById(Long id) {

        ConformiteNorme conformiteNorme = conformiteNormeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conformité introuvable."));

        return conformiteNormeMapper.toResponseDTO(conformiteNorme);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConformiteNormeResponseDTO> getAll() {

        return conformiteNormeRepository.findAll()
                .stream()
                .map(conformiteNormeMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConformiteNormeResponseDTO> getByEssai(Long idEssai) {

        return conformiteNormeRepository.findByEssaiIdEssai(idEssai)
                .stream()
                .map(conformiteNormeMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConformiteNormeResponseDTO> getByNorme(Long idNorme) {

        return conformiteNormeRepository.findByNormeIdNorme(idNorme)
                .stream()
                .map(conformiteNormeMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        ConformiteNorme conformiteNorme = conformiteNormeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conformité introuvable."));

        conformiteNormeRepository.delete(conformiteNorme);
    }
}