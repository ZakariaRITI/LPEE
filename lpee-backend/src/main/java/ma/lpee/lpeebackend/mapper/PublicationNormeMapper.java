package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.PublicationNormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.PublicationNormeResponseDTO;
import ma.lpee.lpeebackend.entity.Norme;
import ma.lpee.lpeebackend.entity.Organisme;
import ma.lpee.lpeebackend.entity.PublicationNorme;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PublicationNormeMapper {

    /**
     * Conversion de l'entité PublicationNorme vers PublicationNormeResponseDTO.
     * Mappe les objets imbriqués Norme et Organisme vers leurs identifiants simples.
     */
    @Mapping(target = "idNorme", source = "norme.idNorme")
    @Mapping(target = "idOrganisme", source = "organisme.idOrganisme")
    PublicationNormeResponseDTO toResponseDTO(PublicationNorme entity);

    /**
     * Conversion du PublicationNormeRequestDTO vers une nouvelle entité.
     * La clé primaire idPublication et les champs d'audit/historique sont ignorés.
     * Les relations Norme et Organisme sont résolues via les méthodes helper.
     */
    @Mapping(target = "idPublication", ignore = true)
    @Mapping(target = "norme", source = "idNorme")
    @Mapping(target = "organisme", source = "idOrganisme")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    PublicationNorme toEntity(PublicationNormeRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité PublicationNorme existante à partir d'un RequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idPublication", ignore = true)
    @Mapping(target = "norme", source = "idNorme")
    @Mapping(target = "organisme", source = "idOrganisme")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    void updateEntityFromDto(PublicationNormeRequestDTO dto, @MappingTarget PublicationNorme entity);

    /**
     * Convertit un ID de norme en une référence d'entité Norme.
     */
    default Norme mapNorme(Long id) {
        if (id == null) {
            return null;
        }
        Norme norme = new Norme();
        norme.setIdNorme(id);
        return norme;
    }

    /**
     * Convertit un ID d'organisme en une référence d'entité Organisme.
     */
    default Organisme mapOrganisme(Long id) {
        if (id == null) {
            return null;
        }
        Organisme organisme = new Organisme();
        organisme.setIdOrganisme(id);
        return organisme;
    }
}