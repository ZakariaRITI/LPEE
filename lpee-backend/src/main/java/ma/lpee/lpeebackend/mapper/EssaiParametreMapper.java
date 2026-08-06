package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.EssaiParametreRequestDTO;
import ma.lpee.lpeebackend.dto.response.EssaiParametreResponseDTO;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.entity.EssaiParametre;
import ma.lpee.lpeebackend.entity.Parametre;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EssaiParametreMapper {

    /**
     * Conversion de l'entité EssaiParametre vers EssaiParametreResponseDTO.
     * Mappe les objets imbriqués Essai et Parametre vers leurs identifiants simples.
     */
    @Mapping(target = "idEssai", source = "essai.idEssai")
    @Mapping(target = "idParametre", source = "parametre.idParametre")
    EssaiParametreResponseDTO toResponseDTO(EssaiParametre entity);

    /**
     * Conversion du EssaiParametreRequestDTO vers une nouvelle entité.
     * La clé primaire idMesure ainsi que les champs d'audit système sont ignorés.
     * Les relations Essai et Parametre sont résolues via les méthodes helper.
     */
    @Mapping(target = "idMesure", ignore = true)
    @Mapping(target = "essai", source = "idEssai")
    @Mapping(target = "parametre", source = "idParametre")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    EssaiParametre toEntity(EssaiParametreRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité EssaiParametre existante à partir d'un RequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idMesure", ignore = true)
    @Mapping(target = "essai", source = "idEssai")
    @Mapping(target = "parametre", source = "idParametre")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    void updateEntityFromDto(EssaiParametreRequestDTO dto, @MappingTarget EssaiParametre entity);

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
     * Convertit un ID de paramètre en une référence d'entité Parametre.
     */
    default Parametre mapParametre(Long id) {
        if (id == null) {
            return null;
        }
        Parametre parametre = new Parametre();
        parametre.setIdParametre(id);
        return parametre;
    }
}