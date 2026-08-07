package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.RoleRequestDTO;
import ma.lpee.lpeebackend.dto.response.RoleResponseDTO;

import java.util.List;

public interface RoleService {

    RoleResponseDTO create(RoleRequestDTO dto);

    RoleResponseDTO findById(Long id);

    List<RoleResponseDTO> findAll();

    RoleResponseDTO update(Long id, RoleRequestDTO dto);

    void delete(Long id);

    RoleResponseDTO findByCodeRole(String codeRole);
}