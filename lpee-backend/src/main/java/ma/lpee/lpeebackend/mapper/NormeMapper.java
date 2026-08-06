package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.NormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.NormeResponseDTO;
import ma.lpee.lpeebackend.entity.Norme;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NormeMapper {

    /**
     * Conversion de l'entité Norme vers NormeResponseDTO.
     * La collection inverse documents (@ManyToMany) est absente du DTO de réponse, elle est donc omise.
     */
    NormeResponseDTO toResponseDTO(Norme entity);

    /**
     * Conversion du NormeRequestDTO vers une nouvelle entité Norme.
     * La clé primaire idNorme et la collection inverse documents sont ignorées.
     */
    @Mapping(target = "idNorme", ignore = true)
    @Mapping(target = "documents", ignore = true)
    Norme toEntity(NormeRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Norme existante à partir d'un NormeRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idNorme", ignore = true)
    @Mapping(target = "documents", ignore = true)
    void updateEntityFromDto(NormeRequestDTO dto, @MappingTarget Norme entity);
}