package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.ProduitRequestDTO;
import ma.lpee.lpeebackend.dto.response.ProduitResponseDTO;
import ma.lpee.lpeebackend.entity.FamilleProduit;
import ma.lpee.lpeebackend.entity.Produit;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    /**
     * Conversion de l'entité Produit vers ProduitResponseDTO.
     * Mappe la relation FamilleProduit vers l'identifiant idFamille.
     */
    @Mapping(target = "idFamille", source = "familleProduit.idFamille")
    ProduitResponseDTO toResponseDTO(Produit entity);

    /**
     * Conversion du ProduitRequestDTO vers une nouvelle entité Produit.
     * La clé primaire idProduit est ignorée.
     * La relation FamilleProduit est résolue via la méthode mapFamilleProduit.
     */
    @Mapping(target = "idProduit", ignore = true)
    @Mapping(target = "familleProduit", source = "idFamille")
    Produit toEntity(ProduitRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Produit existante à partir d'un ProduitRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idProduit", ignore = true)
    @Mapping(target = "familleProduit", source = "idFamille")
    void updateEntityFromDto(ProduitRequestDTO dto, @MappingTarget Produit entity);

    /**
     * Convertit un ID de famille de produit en une référence d'entité FamilleProduit.
     */
    default FamilleProduit mapFamilleProduit(Long id) {
        if (id == null) {
            return null;
        }
        FamilleProduit familleProduit = new FamilleProduit();
        familleProduit.setIdFamille(id);
        return familleProduit;
    }
}