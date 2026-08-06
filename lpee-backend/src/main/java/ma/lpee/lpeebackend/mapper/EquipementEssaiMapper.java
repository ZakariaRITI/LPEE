package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.EquipementEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.EquipementEssaiResponseDTO;
import ma.lpee.lpeebackend.entity.Equipement;
import ma.lpee.lpeebackend.entity.EquipementEssai;
import ma.lpee.lpeebackend.entity.Essai;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EquipementEssaiMapper {

    /**
     * Conversion de l'entité EquipementEssai vers EquipementEssaiResponseDTO.
     * Mappe les objets imbriqués Essai et Equipement vers leurs identifiants simples.
     */
    @Mapping(target = "idEssai", source = "essai.idEssai")
    @Mapping(target = "idEquipement", source = "equipement.idEquipement")
    EquipementEssaiResponseDTO toResponseDTO(EquipementEssai entity);

    /**
     * Conversion du EquipementEssaiRequestDTO vers une nouvelle entité.
     * La clé primaire idUtilisationEquipement et les champs d'audit/historique sont ignorés.
     * Les relations Essai et Equipement sont résolues via les méthodes helper.
     */
    @Mapping(target = "idUtilisationEquipement", ignore = true)
    @Mapping(target = "essai", source = "idEssai")
    @Mapping(target = "equipement", source = "idEquipement")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    EquipementEssai toEntity(EquipementEssaiRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité EquipementEssai existante à partir d'un RequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idUtilisationEquipement", ignore = true)
    @Mapping(target = "essai", source = "idEssai")
    @Mapping(target = "equipement", source = "idEquipement")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    void updateEntityFromDto(EquipementEssaiRequestDTO dto, @MappingTarget EquipementEssai entity);

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
     * Convertit un ID d'équipement en une référence d'entité Equipement.
     */
    default Equipement mapEquipement(Long id) {
        if (id == null) {
            return null;
        }
        Equipement equipement = new Equipement();
        equipement.setIdEquipement(id);
        return equipement;
    }
}