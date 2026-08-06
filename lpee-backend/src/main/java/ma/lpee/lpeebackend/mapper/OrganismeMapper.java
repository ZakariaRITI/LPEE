package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.OrganismeRequestDTO;
import ma.lpee.lpeebackend.dto.response.OrganismeResponseDTO;
import ma.lpee.lpeebackend.entity.Organisme;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrganismeMapper {

    /**
     * Conversion de l'entité Organisme vers OrganismeResponseDTO.
     */
    OrganismeResponseDTO toResponseDTO(Organisme entity);

    /**
     * Conversion du OrganismeRequestDTO vers une nouvelle entité Organisme.
     * La clé primaire idOrganisme est ignorée.
     */
    @Mapping(target = "idOrganisme", ignore = true)
    Organisme toEntity(OrganismeRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Organisme existante à partir d'un OrganismeRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idOrganisme", ignore = true)
    void updateEntityFromDto(OrganismeRequestDTO dto, @MappingTarget Organisme entity);
}