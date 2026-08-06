package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.FamilleProduitRequestDTO;
import ma.lpee.lpeebackend.dto.response.FamilleProduitResponseDTO;
import ma.lpee.lpeebackend.entity.FamilleProduit;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FamilleProduitMapper {

    /**
     * Conversion de l'entité FamilleProduit vers FamilleProduitResponseDTO.
     */
    FamilleProduitResponseDTO toResponseDTO(FamilleProduit entity);

    /**
     * Conversion du FamilleProduitRequestDTO vers une nouvelle entité FamilleProduit.
     * La clé primaire idFamille est ignorée.
     */
    @Mapping(target = "idFamille", ignore = true)
    FamilleProduit toEntity(FamilleProduitRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité FamilleProduit existante à partir d'un FamilleProduitRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idFamille", ignore = true)
    void updateEntityFromDto(FamilleProduitRequestDTO dto, @MappingTarget FamilleProduit entity);
}