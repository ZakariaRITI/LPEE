package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.ConformiteNormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.ConformiteNormeResponseDTO;
import ma.lpee.lpeebackend.entity.ConformiteNorme;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.entity.Norme;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ConformiteNormeMapper {

    /**
     * Conversion de l'entité vers le ResponseDTO.
     * Mappe les objets imbriqués Essai et Norme vers leurs identifiants simples.
     */
    @Mapping(target = "idEssai", source = "essai.idEssai")
    @Mapping(target = "idNorme", source = "norme.idNorme")
    ConformiteNormeResponseDTO toResponseDTO(ConformiteNorme entity);

    /**
     * Conversion du RequestDTO vers une nouvelle entité.
     * La clé primaire idConformite ainsi que les métadonnées de mise à jour/annulation sont ignorées.
     * Les relations Essai et Norme sont résolues via les méthodes helper (mapEssai et mapNorme).
     */
    @Mapping(target = "idConformite", ignore = true)
    @Mapping(target = "essai", source = "idEssai")
    @Mapping(target = "norme", source = "idNorme")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    ConformiteNorme toEntity(ConformiteNormeRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité existante à partir d'un RequestDTO.
     * Conserve la stratégie NullValuePropertyMappingStrategy.IGNORE pour préserver les valeurs non fournies.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idConformite", ignore = true)
    @Mapping(target = "essai", source = "idEssai")
    @Mapping(target = "norme", source = "idNorme")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    void updateEntityFromDto(ConformiteNormeRequestDTO dto, @MappingTarget ConformiteNorme entity);

    /**
     * Convertit un ID d'essai en une référence d'entité Essai.
     */
    default Essai mapEssai(Long id) {
        if (id == null) {
            return null;
        }
        Essai essai = new Essai();
        essai.setIdEssai(id);
        return essai;
    }

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
}