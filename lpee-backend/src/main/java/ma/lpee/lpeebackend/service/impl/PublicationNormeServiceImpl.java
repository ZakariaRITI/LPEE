package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.PublicationNormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.PublicationNormeResponseDTO;
import ma.lpee.lpeebackend.entity.PublicationNorme;
import ma.lpee.lpeebackend.mapper.PublicationNormeMapper;
import ma.lpee.lpeebackend.repository.PublicationNormeRepository;
import ma.lpee.lpeebackend.service.PublicationNormeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PublicationNormeServiceImpl implements PublicationNormeService {

    private final PublicationNormeRepository publicationNormeRepository;
    private final PublicationNormeMapper publicationNormeMapper;

    @Override
    public PublicationNormeResponseDTO create(PublicationNormeRequestDTO dto) {

        PublicationNorme entity = publicationNormeMapper.toEntity(dto);

        entity.setCreeLe(LocalDateTime.now());

        PublicationNorme savedEntity = publicationNormeRepository.save(entity);

        return publicationNormeMapper.toResponseDTO(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PublicationNormeResponseDTO findById(Long id) {

        PublicationNorme entity = publicationNormeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "PublicationNorme introuvable avec l'id : " + id
                ));

        return publicationNormeMapper.toResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicationNormeResponseDTO> findAll() {

        return publicationNormeRepository.findAll()
                .stream()
                .map(publicationNormeMapper::toResponseDTO)
                .toList();
    }

    @Override
    public PublicationNormeResponseDTO update(
            Long id,
            PublicationNormeRequestDTO dto) {

        PublicationNorme entity = publicationNormeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "PublicationNorme introuvable avec l'id : " + id
                ));

        publicationNormeMapper.updateEntityFromDto(dto, entity);

        entity.setModifieLe(LocalDateTime.now());

        PublicationNorme updatedEntity =
                publicationNormeRepository.save(entity);

        return publicationNormeMapper.toResponseDTO(updatedEntity);
    }

    @Override
    public void delete(Long id) {

        PublicationNorme entity = publicationNormeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "PublicationNorme introuvable avec l'id : " + id
                ));

        publicationNormeRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicationNormeResponseDTO> findByNorme(Long idNorme) {

        return publicationNormeRepository.findByNormeIdNorme(idNorme)
                .stream()
                .map(publicationNormeMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicationNormeResponseDTO> findByOrganisme(Long idOrganisme) {

        return publicationNormeRepository.findByOrganismeIdOrganisme(idOrganisme)
                .stream()
                .map(publicationNormeMapper::toResponseDTO)
                .toList();
    }
}