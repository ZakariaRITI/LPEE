package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.ConformiteNormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.ConformiteNormeResponseDTO;
import ma.lpee.lpeebackend.entity.ConformiteNorme;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.entity.Norme;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.ConformiteNormeMapper;
import ma.lpee.lpeebackend.repository.ConformiteNormeRepository;
import ma.lpee.lpeebackend.repository.EssaiRepository;
import ma.lpee.lpeebackend.repository.NormeRepository;
import ma.lpee.lpeebackend.repository.UtilisateurRepository;
import ma.lpee.lpeebackend.service.ConformiteNormeService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public ConformiteNormeResponseDTO create(ConformiteNormeRequestDTO requestDTO) {

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new ResourceNotFoundException("Essai introuvable."));

        Norme norme = normeRepository.findById(requestDTO.getIdNorme())
                .orElseThrow(() -> new ResourceNotFoundException("Norme introuvable."));

        if (conformiteNormeRepository.existsByEssaiIdEssaiAndNormeIdNorme(
                requestDTO.getIdEssai(),
                requestDTO.getIdNorme())) {
            throw new DuplicateResourceException("Cette conformité existe déjà.");
        }

        ConformiteNorme conformiteNorme = conformiteNormeMapper.toEntity(requestDTO);

        conformiteNorme.setEssai(essai);
        conformiteNorme.setNorme(norme);
        conformiteNorme.setCreeLe(LocalDateTime.now());
        conformiteNorme.setCreePar(getAuthenticatedUserId());

        ConformiteNorme saved = conformiteNormeRepository.save(conformiteNorme);

        return conformiteNormeMapper.toResponseDTO(saved);
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Utilisateur authentifié introuvable.");
        }
        return utilisateurRepository.findByMatricule(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur authentifié introuvable."))
                .getIdUser();
    }

    @Override
    public ConformiteNormeResponseDTO update(Long id, ConformiteNormeRequestDTO requestDTO) {

        ConformiteNorme conformiteNorme = conformiteNormeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conformité introuvable."));

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new ResourceNotFoundException("Essai introuvable."));

        Norme norme = normeRepository.findById(requestDTO.getIdNorme())
                .orElseThrow(() -> new ResourceNotFoundException("Norme introuvable."));

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
                .orElseThrow(() -> new ResourceNotFoundException("Conformité introuvable."));

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
                .orElseThrow(() -> new ResourceNotFoundException("Conformité introuvable."));

        conformiteNormeRepository.delete(conformiteNorme);
    }
}
