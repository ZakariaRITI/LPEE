package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.OrganismeRequestDTO;
import ma.lpee.lpeebackend.dto.response.OrganismeResponseDTO;
import ma.lpee.lpeebackend.entity.Organisme;
import ma.lpee.lpeebackend.mapper.OrganismeMapper;
import ma.lpee.lpeebackend.repository.OrganismeRepository;
import ma.lpee.lpeebackend.service.OrganismeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganismeServiceImpl implements OrganismeService {

    private final OrganismeRepository organismeRepository;
    private final OrganismeMapper organismeMapper;

    @Override
    public OrganismeResponseDTO create(OrganismeRequestDTO requestDTO) {

        if (organismeRepository.existsByCodeOrganisme(requestDTO.getCodeOrganisme())) {
            throw new RuntimeException("Un organisme avec ce code existe déjà.");
        }

        Organisme organisme = organismeMapper.toEntity(requestDTO);

        Organisme saved = organismeRepository.save(organisme);

        return organismeMapper.toResponseDTO(saved);
    }

    @Override
    public OrganismeResponseDTO update(Long id, OrganismeRequestDTO requestDTO) {

        Organisme organisme = organismeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisme introuvable."));

        if (!organisme.getCodeOrganisme().equals(requestDTO.getCodeOrganisme())
                && organismeRepository.existsByCodeOrganisme(requestDTO.getCodeOrganisme())) {
            throw new RuntimeException("Un organisme avec ce code existe déjà.");
        }

        organismeMapper.updateEntityFromDto(requestDTO, organisme);

        Organisme updated = organismeRepository.save(organisme);

        return organismeMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganismeResponseDTO getById(Long id) {

        Organisme organisme = organismeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisme introuvable."));

        return organismeMapper.toResponseDTO(organisme);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganismeResponseDTO> getAll() {

        return organismeRepository.findAll()
                .stream()
                .map(organismeMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Organisme organisme = organismeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisme introuvable."));

        organismeRepository.delete(organisme);
    }
}