package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.PublicationNormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.PublicationNormeResponseDTO;

import java.util.List;

public interface PublicationNormeService {

    PublicationNormeResponseDTO create(PublicationNormeRequestDTO dto);

    PublicationNormeResponseDTO findById(Long id);

    List<PublicationNormeResponseDTO> findAll();

    PublicationNormeResponseDTO update(Long id, PublicationNormeRequestDTO dto);

    void delete(Long id);

    List<PublicationNormeResponseDTO> findByNorme(Long idNorme);

    List<PublicationNormeResponseDTO> findByOrganisme(Long idOrganisme);
}