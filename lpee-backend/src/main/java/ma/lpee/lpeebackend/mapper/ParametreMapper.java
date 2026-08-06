package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.ParametreRequestDTO;
import ma.lpee.lpeebackend.dto.response.ParametreResponseDTO;
import ma.lpee.lpeebackend.entity.Parametre;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ParametreMapper {

    /**
     * Conversion de l'entité Parametre vers ParametreResponseDTO.
     */
    ParametreResponseDTO toResponseDTO(Parametre entity);

    /**
     * Conversion du ParametreRequestDTO vers une nouvelle entité Parametre.
     * La clé primaire idParametre est ignorée.
     */
    @Mapping(target = "idParametre", ignore = true)
    Parametre toEntity(ParametreRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Parametre existante à partir d'un ParametreRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idParametre", ignore = true)
    void updateEntityFromDto(ParametreRequestDTO dto, @MappingTarget Parametre entity);
}