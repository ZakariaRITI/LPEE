package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.RealisationEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.RealisationEssaiResponseDTO;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.entity.RealisationEssai;
import ma.lpee.lpeebackend.entity.Unite;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RealisationEssaiMapper {

    /**
     * Conversion de l'entité RealisationEssai vers RealisationEssaiResponseDTO.
     * Mappe les objets imbriqués Unite et Essai vers leurs identifiants simples.
     */
    @Mapping(target = "idUnite", source = "unite.idUnite")
    @Mapping(target = "idEssai", source = "essai.idEssai")
    RealisationEssaiResponseDTO toResponseDTO(RealisationEssai entity);

    /**
     * Conversion du RealisationEssaiRequestDTO vers une nouvelle entité.
     * La clé primaire idRealisation et les champs d'audit système sont ignorés.
     * Les relations Unite et Essai sont résolues via les méthodes helper.
     */
    @Mapping(target = "idRealisation", ignore = true)
    @Mapping(target = "unite", source = "idUnite")
    @Mapping(target = "essai", source = "idEssai")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    RealisationEssai toEntity(RealisationEssaiRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité RealisationEssai existante à partir d'un RequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idRealisation", ignore = true)
    @Mapping(target = "unite", source = "idUnite")
    @Mapping(target = "essai", source = "idEssai")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    void updateEntityFromDto(RealisationEssaiRequestDTO dto, @MappingTarget RealisationEssai entity);

    /**
     * Convertit un ID d'unité en une référence d'entité Unite.
     */
    default Unite mapUnite(Long id) {
        if (id == null) {
            return null;
        }
        Unite unite = new Unite();
        unite.setIdUnite(id);
        return unite;
    }

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
}