package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.OrganismeRequestDTO;
import ma.lpee.lpeebackend.dto.response.OrganismeResponseDTO;

import java.util.List;

public interface OrganismeService {

    OrganismeResponseDTO create(OrganismeRequestDTO requestDTO);

    OrganismeResponseDTO update(Long id, OrganismeRequestDTO requestDTO);

    OrganismeResponseDTO getById(Long id);

    List<OrganismeResponseDTO> getAll();

    void delete(Long id);
}