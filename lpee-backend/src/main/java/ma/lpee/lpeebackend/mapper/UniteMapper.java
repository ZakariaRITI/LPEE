package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.UniteRequestDTO;
import ma.lpee.lpeebackend.dto.response.UniteResponseDTO;
import ma.lpee.lpeebackend.entity.Region;
import ma.lpee.lpeebackend.entity.Unite;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UniteMapper {

    /**
     * Conversion de l'entité Unite vers UniteResponseDTO.
     * Mappe l'objet imbriqué Region vers son identifiant simple.
     */
    @Mapping(target = "idRegion", source = "region.idRegion")
    UniteResponseDTO toResponseDTO(Unite entity);

    /**
     * Conversion du UniteRequestDTO vers une nouvelle entité Unite.
     * La clé primaire idUnite est ignorée.
     * La relation Region est résolue via la méthode helper.
     */
    @Mapping(target = "idUnite", ignore = true)
    @Mapping(target = "region", source = "idRegion")
    Unite toEntity(UniteRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Unite existante à partir d'un UniteRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idUnite", ignore = true)
    @Mapping(target = "region", source = "idRegion")
    void updateEntityFromDto(UniteRequestDTO dto, @MappingTarget Unite entity);

    /**
     * Convertit un ID de région en une référence d'entité Region.
     */
    default Region mapRegion(Long id) {
        if (id == null) {
            return null;
        }
        Region region = new Region();
        region.setIdRegion(id);
        return region;
    }
}