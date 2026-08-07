package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.EssaiParametreRequestDTO;
import ma.lpee.lpeebackend.dto.response.EssaiParametreResponseDTO;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.entity.EssaiParametre;
import ma.lpee.lpeebackend.entity.Parametre;
import ma.lpee.lpeebackend.mapper.EssaiParametreMapper;
import ma.lpee.lpeebackend.repository.EssaiParametreRepository;
import ma.lpee.lpeebackend.repository.EssaiRepository;
import ma.lpee.lpeebackend.repository.ParametreRepository;
import ma.lpee.lpeebackend.service.EssaiParametreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EssaiParametreServiceImpl implements EssaiParametreService {

    private final EssaiParametreRepository essaiParametreRepository;
    private final EssaiRepository essaiRepository;
    private final ParametreRepository parametreRepository;
    private final EssaiParametreMapper essaiParametreMapper;

    @Override
    public EssaiParametreResponseDTO create(EssaiParametreRequestDTO requestDTO) {

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new RuntimeException("Essai introuvable."));

        Parametre parametre = parametreRepository.findById(requestDTO.getIdParametre())
                .orElseThrow(() -> new RuntimeException("Paramètre introuvable."));

        if (essaiParametreRepository.existsByEssaiIdEssaiAndParametreIdParametre(
                requestDTO.getIdEssai(),
                requestDTO.getIdParametre())) {
            throw new RuntimeException("Cette association essai-paramètre existe déjà.");
        }

        EssaiParametre essaiParametre = essaiParametreMapper.toEntity(requestDTO);

        essaiParametre.setEssai(essai);
        essaiParametre.setParametre(parametre);
        essaiParametre.setCreeLe(LocalDateTime.now());

        EssaiParametre saved = essaiParametreRepository.save(essaiParametre);

        return essaiParametreMapper.toResponseDTO(saved);
    }

    @Override
    public EssaiParametreResponseDTO update(Long id, EssaiParametreRequestDTO requestDTO) {

        EssaiParametre essaiParametre = essaiParametreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association essai-paramètre introuvable."));

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new RuntimeException("Essai introuvable."));

        Parametre parametre = parametreRepository.findById(requestDTO.getIdParametre())
                .orElseThrow(() -> new RuntimeException("Paramètre introuvable."));

        essaiParametreMapper.updateEntityFromDto(requestDTO, essaiParametre);

        essaiParametre.setEssai(essai);
        essaiParametre.setParametre(parametre);
        essaiParametre.setModifieLe(LocalDateTime.now());

        EssaiParametre updated = essaiParametreRepository.save(essaiParametre);

        return essaiParametreMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EssaiParametreResponseDTO getById(Long id) {

        EssaiParametre essaiParametre = essaiParametreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association essai-paramètre introuvable."));

        return essaiParametreMapper.toResponseDTO(essaiParametre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EssaiParametreResponseDTO> getAll() {

        return essaiParametreRepository.findAll()
                .stream()
                .map(essaiParametreMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EssaiParametreResponseDTO> getByEssai(Long idEssai) {

        return essaiParametreRepository.findByEssaiIdEssai(idEssai)
                .stream()
                .map(essaiParametreMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EssaiParametreResponseDTO> getByParametre(Long idParametre) {

        return essaiParametreRepository.findByParametreIdParametre(idParametre)
                .stream()
                .map(essaiParametreMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        EssaiParametre essaiParametre = essaiParametreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Association essai-paramètre introuvable."));

        essaiParametreRepository.delete(essaiParametre);
    }
}