package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.EquipementRequestDTO;
import ma.lpee.lpeebackend.dto.response.EquipementResponseDTO;
import ma.lpee.lpeebackend.entity.Equipement;
import ma.lpee.lpeebackend.entity.Marque;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EquipementMapper {

    /**
     * Conversion de l'entité Equipement vers EquipementResponseDTO.
     * Mappe la relation Marque vers l'identifiant idMarque.
     */
    @Mapping(target = "idMarque", source = "marque.idMarque")
    EquipementResponseDTO toResponseDTO(Equipement entity);

    /**
     * Conversion du EquipementRequestDTO vers une nouvelle entité Equipement.
     * La clé primaire idEquipement est ignorée.
     * La relation Marque est résolue via la méthode mapMarque.
     */
    @Mapping(target = "idEquipement", ignore = true)
    @Mapping(target = "marque", source = "idMarque")
    Equipement toEntity(EquipementRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Equipement existante à partir d'un EquipementRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idEquipement", ignore = true)
    @Mapping(target = "marque", source = "idMarque")
    void updateEntityFromDto(EquipementRequestDTO dto, @MappingTarget Equipement entity);

    /**
     * Convertit un ID de marque en une référence d'entité Marque.
     */
    default Marque mapMarque(Long id) {
        if (id == null) {
            return null;
        }
        Marque marque = new Marque();
        marque.setIdMarque(id);
        return marque;
    }
}