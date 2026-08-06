package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.EssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.EssaiResponseDTO;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.entity.Produit;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EssaiMapper {

    /**
     * Conversion de l'entité Essai vers EssaiResponseDTO.
     * Mappe la relation Produit vers l'identifiant idProduit.
     */
    @Mapping(target = "idProduit", source = "produit.idProduit")
    EssaiResponseDTO toResponseDTO(Essai entity);

    /**
     * Conversion du EssaiRequestDTO vers une nouvelle entité Essai.
     * La clé primaire idEssai est ignorée.
     * La relation Produit est résolue via la méthode mapProduit.
     */
    @Mapping(target = "idEssai", ignore = true)
    @Mapping(target = "produit", source = "idProduit")
    Essai toEntity(EssaiRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Essai existante à partir d'un EssaiRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idEssai", ignore = true)
    @Mapping(target = "produit", source = "idProduit")
    void updateEntityFromDto(EssaiRequestDTO dto, @MappingTarget Essai entity);

    /**
     * Convertit un ID de produit en une référence d'entité Produit.
     */
    default Produit mapProduit(Long id) {
        if (id == null) {
            return null;
        }
        Produit produit = new Produit();
        produit.setIdProduit(id);
        return produit;
    }
}