package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.RoleRequestDTO;
import ma.lpee.lpeebackend.dto.response.RoleResponseDTO;
import ma.lpee.lpeebackend.entity.Role;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.RoleMapper;
import ma.lpee.lpeebackend.repository.RoleRepository;
import ma.lpee.lpeebackend.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponseDTO create(RoleRequestDTO dto) {

        Role entity = roleMapper.toEntity(dto);

        Role savedEntity = roleRepository.save(entity);

        return roleMapper.toResponseDTO(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDTO findById(Long id) {

        Role entity = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rôle introuvable avec l'id : " + id
                ));

        return roleMapper.toResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> findAll() {

        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponseDTO)
                .toList();
    }

    @Override
    public RoleResponseDTO update(Long id, RoleRequestDTO dto) {

        Role entity = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rôle introuvable avec l'id : " + id
                ));

        roleMapper.updateEntityFromDto(dto, entity);

        Role updatedEntity = roleRepository.save(entity);

        return roleMapper.toResponseDTO(updatedEntity);
    }

    @Override
    public void delete(Long id) {

        Role entity = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rôle introuvable avec l'id : " + id
                ));

        roleRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDTO findByCodeRole(String codeRole) {

        Role entity = roleRepository.findByCodeRole(codeRole)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rôle introuvable avec le code : " + codeRole
                ));

        return roleMapper.toResponseDTO(entity);
    }
}