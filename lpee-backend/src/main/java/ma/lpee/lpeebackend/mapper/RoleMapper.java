package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.RoleRequestDTO;
import ma.lpee.lpeebackend.dto.response.RoleResponseDTO;
import ma.lpee.lpeebackend.entity.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    /**
     * Conversion de l'entité Role vers RoleResponseDTO.
     */
    RoleResponseDTO toResponseDTO(Role entity);

    /**
     * Conversion du RoleRequestDTO vers une nouvelle entité Role.
     * La clé primaire idRole est ignorée.
     */
    @Mapping(target = "idRole", ignore = true)
    Role toEntity(RoleRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Role existante à partir d'un RoleRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idRole", ignore = true)
    void updateEntityFromDto(RoleRequestDTO dto, @MappingTarget Role entity);
}