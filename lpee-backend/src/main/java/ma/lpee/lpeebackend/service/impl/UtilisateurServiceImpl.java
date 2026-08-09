package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.UtilisateurRequestDTO;
import ma.lpee.lpeebackend.dto.response.UtilisateurResponseDTO;
import ma.lpee.lpeebackend.entity.Utilisateur;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.UtilisateurMapper;
import ma.lpee.lpeebackend.repository.RoleRepository;
import ma.lpee.lpeebackend.repository.UniteRepository;
import ma.lpee.lpeebackend.repository.UtilisateurRepository;
import ma.lpee.lpeebackend.service.UtilisateurService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final UniteRepository uniteRepository;
    private final UtilisateurMapper utilisateurMapper;

    @Override
    public UtilisateurResponseDTO create(UtilisateurRequestDTO dto) {

        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "Un utilisateur avec l'email '" + dto.getEmail() + "' existe déjà"
            );
        }

        if (!roleRepository.existsById(dto.getIdRole())) {
            throw new ResourceNotFoundException(
                    "Le rôle avec l'ID " + dto.getIdRole() + " n'existe pas"
            );
        }

        if (!uniteRepository.existsById(dto.getIdUnite())) {
            throw new ResourceNotFoundException(
                    "L'unité avec l'ID " + dto.getIdUnite() + " n'existe pas"
            );
        }

        Utilisateur entity = utilisateurMapper.toEntity(dto);

        Utilisateur saved = utilisateurRepository.save(entity);

        return utilisateurMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponseDTO getById(Long id) {

        Utilisateur entity = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable avec l'ID " + id
                ));

        return utilisateurMapper.toResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponseDTO> getAll() {

        return utilisateurRepository.findAll()
                .stream()
                .map(utilisateurMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UtilisateurResponseDTO update(Long id, UtilisateurRequestDTO dto) {

        Utilisateur entity = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable avec l'ID " + id
                ));

        if (!entity.getEmail().equals(dto.getEmail())
                && utilisateurRepository.existsByEmail(dto.getEmail())) {

            throw new DuplicateResourceException(
                    "Un utilisateur avec l'email '" + dto.getEmail() + "' existe déjà"
            );
        }

        if (!roleRepository.existsById(dto.getIdRole())) {
            throw new ResourceNotFoundException(
                    "Le rôle avec l'ID " + dto.getIdRole() + " n'existe pas"
            );
        }

        if (!uniteRepository.existsById(dto.getIdUnite())) {
            throw new ResourceNotFoundException(
                    "L'unité avec l'ID " + dto.getIdUnite() + " n'existe pas"
            );
        }

        utilisateurMapper.updateEntityFromDto(dto, entity);

        Utilisateur updated = utilisateurRepository.save(entity);

        return utilisateurMapper.toResponseDTO(updated);
    }

    @Override
    public void delete(Long id) {

        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Utilisateur introuvable avec l'ID " + id
            );
        }

        utilisateurRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponseDTO> getByUnite(Long idUnite) {

        if (!uniteRepository.existsById(idUnite)) {
            throw new ResourceNotFoundException(
                    "L'unité avec l'ID " + idUnite + " n'existe pas"
            );
        }

        return utilisateurRepository.findByUniteIdUnite(idUnite)
                .stream()
                .map(utilisateurMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurResponseDTO> getByRole(Long idRole) {

        if (!roleRepository.existsById(idRole)) {
            throw new ResourceNotFoundException(
                    "Le rôle avec l'ID " + idRole + " n'existe pas"
            );
        }

        return utilisateurRepository.findByRoleIdRole(idRole)
                .stream()
                .map(utilisateurMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurResponseDTO getByEmail(String email) {

        Utilisateur entity = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable avec l'email '" + email + "'"
                ));

        return utilisateurMapper.toResponseDTO(entity);
    }
}