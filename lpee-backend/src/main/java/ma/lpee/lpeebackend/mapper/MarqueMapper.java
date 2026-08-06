package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.MarqueRequestDTO;
import ma.lpee.lpeebackend.dto.response.MarqueResponseDTO;
import ma.lpee.lpeebackend.entity.Marque;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MarqueMapper {

    /**
     * Conversion de l'entité Marque vers MarqueResponseDTO.
     */
    MarqueResponseDTO toResponseDTO(Marque entity);

    /**
     * Conversion du MarqueRequestDTO vers une nouvelle entité Marque.
     * La clé primaire idMarque est ignorée.
     */
    @Mapping(target = "idMarque", ignore = true)
    Marque toEntity(MarqueRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Marque existante à partir d'un MarqueRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idMarque", ignore = true)
    void updateEntityFromDto(MarqueRequestDTO dto, @MappingTarget Marque entity);
}