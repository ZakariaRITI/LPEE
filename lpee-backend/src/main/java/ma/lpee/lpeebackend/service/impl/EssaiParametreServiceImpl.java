package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.EssaiParametreRequestDTO;
import ma.lpee.lpeebackend.dto.response.EssaiParametreResponseDTO;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.entity.EssaiParametre;
import ma.lpee.lpeebackend.entity.Parametre;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.EssaiParametreMapper;
import ma.lpee.lpeebackend.repository.EssaiParametreRepository;
import ma.lpee.lpeebackend.repository.EssaiRepository;
import ma.lpee.lpeebackend.repository.ParametreRepository;
import ma.lpee.lpeebackend.repository.UtilisateurRepository;
import ma.lpee.lpeebackend.service.EssaiParametreService;
import ma.lpee.lpeebackend.security.AuthenticatedUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UtilisateurRepository utilisateurRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public EssaiParametreResponseDTO create(EssaiParametreRequestDTO requestDTO) {

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new ResourceNotFoundException("Essai introuvable."));

        Parametre parametre = parametreRepository.findById(requestDTO.getIdParametre())
                .orElseThrow(() -> new ResourceNotFoundException("Paramètre introuvable."));

        if (essaiParametreRepository.existsByEssaiIdEssaiAndParametreIdParametre(
                requestDTO.getIdEssai(),
                requestDTO.getIdParametre())) {
            throw new DuplicateResourceException(
                    "Cette association essai-paramètre existe déjà."
            );
        }

        EssaiParametre essaiParametre = essaiParametreMapper.toEntity(requestDTO);

        essaiParametre.setEssai(essai);
        essaiParametre.setParametre(parametre);
        essaiParametre.setCreeLe(LocalDateTime.now());
        essaiParametre.setCreePar(getAuthenticatedUserId());

        EssaiParametre saved = essaiParametreRepository.save(essaiParametre);

        return essaiParametreMapper.toResponseDTO(saved);
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
    public EssaiParametreResponseDTO update(Long id, EssaiParametreRequestDTO requestDTO) {

        EssaiParametre essaiParametre = essaiParametreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Association essai-paramètre introuvable."
                ));

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new ResourceNotFoundException("Essai introuvable."));

        Parametre parametre = parametreRepository.findById(requestDTO.getIdParametre())
                .orElseThrow(() -> new ResourceNotFoundException("Paramètre introuvable."));

        essaiParametreMapper.updateEntityFromDto(requestDTO, essaiParametre);

        essaiParametre.setEssai(essai);
        essaiParametre.setParametre(parametre);
        Long authenticatedUserId = authenticatedUserService.getAuthenticatedUserId();
        essaiParametre.setModifieLe(LocalDateTime.now());
        essaiParametre.setModifiePar(authenticatedUserId);
        if ("INACTIF".equalsIgnoreCase(essaiParametre.getStatut())) {
            essaiParametre.setAnnuleLe(LocalDateTime.now());
            essaiParametre.setAnnulePar(authenticatedUserId);
        }

        EssaiParametre updated = essaiParametreRepository.save(essaiParametre);

        return essaiParametreMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EssaiParametreResponseDTO getById(Long id) {

        EssaiParametre essaiParametre = essaiParametreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Association essai-paramètre introuvable."
                ));

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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Association essai-paramètre introuvable."
                ));

        essaiParametre.setStatut("INACTIF");
        essaiParametre.setAnnuleLe(LocalDateTime.now());
        essaiParametre.setAnnulePar(authenticatedUserService.getAuthenticatedUserId());
        essaiParametreRepository.save(essaiParametre);
    }
}
