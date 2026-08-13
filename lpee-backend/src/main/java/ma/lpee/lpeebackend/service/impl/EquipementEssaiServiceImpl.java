package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.EquipementEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.EquipementEssaiResponseDTO;
import ma.lpee.lpeebackend.entity.Equipement;
import ma.lpee.lpeebackend.entity.EquipementEssai;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.EquipementEssaiMapper;
import ma.lpee.lpeebackend.repository.EquipementEssaiRepository;
import ma.lpee.lpeebackend.repository.EquipementRepository;
import ma.lpee.lpeebackend.repository.EssaiRepository;
import ma.lpee.lpeebackend.repository.UtilisateurRepository;
import ma.lpee.lpeebackend.service.EquipementEssaiService;
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
public class EquipementEssaiServiceImpl implements EquipementEssaiService {

    private final EquipementEssaiRepository equipementEssaiRepository;
    private final EssaiRepository essaiRepository;
    private final EquipementRepository equipementRepository;
    private final EquipementEssaiMapper equipementEssaiMapper;
    private final UtilisateurRepository utilisateurRepository;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public EquipementEssaiResponseDTO create(EquipementEssaiRequestDTO requestDTO) {

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new ResourceNotFoundException("Essai introuvable."));

        Equipement equipement = equipementRepository.findById(requestDTO.getIdEquipement())
                .orElseThrow(() -> new ResourceNotFoundException("Équipement introuvable."));

        if (equipementEssaiRepository.existsByEssaiIdEssaiAndEquipementIdEquipement(
                requestDTO.getIdEssai(),
                requestDTO.getIdEquipement())) {
            throw new DuplicateResourceException("Cette utilisation d'équipement existe déjà.");
        }

        EquipementEssai equipementEssai = equipementEssaiMapper.toEntity(requestDTO);

        equipementEssai.setEssai(essai);
        equipementEssai.setEquipement(equipement);
        equipementEssai.setCreeLe(LocalDateTime.now());
        equipementEssai.setCreePar(getAuthenticatedUserId());

        EquipementEssai saved = equipementEssaiRepository.save(equipementEssai);

        return equipementEssaiMapper.toResponseDTO(saved);
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
    public EquipementEssaiResponseDTO update(Long id, EquipementEssaiRequestDTO requestDTO) {

        EquipementEssai equipementEssai = equipementEssaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisation d'équipement introuvable."));

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new ResourceNotFoundException("Essai introuvable."));

        Equipement equipement = equipementRepository.findById(requestDTO.getIdEquipement())
                .orElseThrow(() -> new ResourceNotFoundException("Équipement introuvable."));

        equipementEssaiMapper.updateEntityFromDto(requestDTO, equipementEssai);

        equipementEssai.setEssai(essai);
        equipementEssai.setEquipement(equipement);
        Long authenticatedUserId = authenticatedUserService.getAuthenticatedUserId();
        equipementEssai.setModifieLe(LocalDateTime.now());
        equipementEssai.setModifiePar(authenticatedUserId);
        if ("INACTIF".equalsIgnoreCase(equipementEssai.getStatut())) {
            equipementEssai.setAnnuleLe(LocalDateTime.now());
            equipementEssai.setAnnulePar(authenticatedUserId);
        }

        EquipementEssai updated = equipementEssaiRepository.save(equipementEssai);

        return equipementEssaiMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipementEssaiResponseDTO getById(Long id) {

        EquipementEssai equipementEssai = equipementEssaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisation d'équipement introuvable."));

        return equipementEssaiMapper.toResponseDTO(equipementEssai);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipementEssaiResponseDTO> getAll() {

        return equipementEssaiRepository.findAll()
                .stream()
                .map(equipementEssaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipementEssaiResponseDTO> getByEssai(Long idEssai) {

        return equipementEssaiRepository.findByEssaiIdEssai(idEssai)
                .stream()
                .map(equipementEssaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipementEssaiResponseDTO> getByEquipement(Long idEquipement) {

        return equipementEssaiRepository.findByEquipementIdEquipement(idEquipement)
                .stream()
                .map(equipementEssaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        EquipementEssai equipementEssai = equipementEssaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisation d'équipement introuvable."));

        equipementEssai.setStatut("INACTIF");
        equipementEssai.setAnnuleLe(LocalDateTime.now());
        equipementEssai.setAnnulePar(authenticatedUserService.getAuthenticatedUserId());
        equipementEssaiRepository.save(equipementEssai);
    }
}
