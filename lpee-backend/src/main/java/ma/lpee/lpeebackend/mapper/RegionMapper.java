package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.RegionRequestDTO;
import ma.lpee.lpeebackend.dto.response.RegionResponseDTO;
import ma.lpee.lpeebackend.entity.Region;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RegionMapper {

    /**
     * Conversion de l'entité Region vers RegionResponseDTO.
     */
    RegionResponseDTO toResponseDTO(Region entity);

    /**
     * Conversion du RegionRequestDTO vers une nouvelle entité Region.
     * La clé primaire idRegion est ignorée.
     */
    @Mapping(target = "idRegion", ignore = true)
    Region toEntity(RegionRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Region existante à partir d'un RegionRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idRegion", ignore = true)
    void updateEntityFromDto(RegionRequestDTO dto, @MappingTarget Region entity);
}