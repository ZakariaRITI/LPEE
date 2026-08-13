package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.RealisationEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.RealisationEssaiResponseDTO;
import ma.lpee.lpeebackend.entity.RealisationEssai;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.RealisationEssaiMapper;
import ma.lpee.lpeebackend.repository.RealisationEssaiRepository;
import ma.lpee.lpeebackend.repository.UtilisateurRepository;
import ma.lpee.lpeebackend.service.RealisationEssaiService;
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
public class RealisationEssaiServiceImpl implements RealisationEssaiService {

    private final RealisationEssaiRepository realisationEssaiRepository;
    private final RealisationEssaiMapper realisationEssaiMapper;
    private final UtilisateurRepository utilisateurRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public RealisationEssaiResponseDTO create(RealisationEssaiRequestDTO dto) {

        RealisationEssai entity = realisationEssaiMapper.toEntity(dto);

        entity.setCreeLe(LocalDateTime.now());
        entity.setCreePar(getAuthenticatedUserId());

        RealisationEssai savedEntity = realisationEssaiRepository.save(entity);

        return realisationEssaiMapper.toResponseDTO(savedEntity);
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
    @Transactional(readOnly = true)
    public RealisationEssaiResponseDTO findById(Long id) {

        RealisationEssai entity = realisationEssaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réalisation d'essai introuvable avec l'id : " + id
                ));

        return realisationEssaiMapper.toResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RealisationEssaiResponseDTO> findAll() {

        return realisationEssaiRepository.findAll()
                .stream()
                .map(realisationEssaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    public RealisationEssaiResponseDTO update(
            Long id,
            RealisationEssaiRequestDTO dto) {

        RealisationEssai entity = realisationEssaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réalisation d'essai introuvable avec l'id : " + id
                ));

        realisationEssaiMapper.updateEntityFromDto(dto, entity);

        Long authenticatedUserId = authenticatedUserService.getAuthenticatedUserId();
        entity.setModifieLe(LocalDateTime.now());
        entity.setModifiePar(authenticatedUserId);
        if ("INACTIF".equalsIgnoreCase(entity.getStatut())) {
            entity.setAnnuleLe(LocalDateTime.now());
            entity.setAnnulePar(authenticatedUserId);
        }

        RealisationEssai updatedEntity =
                realisationEssaiRepository.save(entity);

        return realisationEssaiMapper.toResponseDTO(updatedEntity);
    }

    @Override
    public void delete(Long id) {

        RealisationEssai entity = realisationEssaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réalisation d'essai introuvable avec l'id : " + id
                ));

        entity.setStatut("INACTIF");
        entity.setAnnuleLe(LocalDateTime.now());
        entity.setAnnulePar(authenticatedUserService.getAuthenticatedUserId());
        realisationEssaiRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RealisationEssaiResponseDTO> findByEssai(Long idEssai) {

        return realisationEssaiRepository.findByEssaiIdEssai(idEssai)
                .stream()
                .map(realisationEssaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RealisationEssaiResponseDTO> findByUnite(Long idUnite) {

        return realisationEssaiRepository.findByUniteIdUnite(idUnite)
                .stream()
                .map(realisationEssaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RealisationEssaiResponseDTO> findByCreePar(Long idUser) {

        return realisationEssaiRepository.findByCreePar(idUser)
                .stream()
                .map(realisationEssaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RealisationEssaiResponseDTO> findByStatut(String statut) {

        return realisationEssaiRepository.findByStatut(statut)
                .stream()
                .map(realisationEssaiMapper::toResponseDTO)
                .toList();
    }
}
