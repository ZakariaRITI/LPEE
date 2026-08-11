package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.UtilisateurRequestDTO;
import ma.lpee.lpeebackend.dto.response.UtilisateurResponseDTO;
import ma.lpee.lpeebackend.entity.Role;
import ma.lpee.lpeebackend.entity.Unite;
import ma.lpee.lpeebackend.entity.Utilisateur;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    /**
     * Conversion de l'entité Utilisateur vers UtilisateurResponseDTO.
     * Mappe les objets imbriqués Role et Unite vers leurs identifiants simples.
     * Le champ sensible motDePasse n'est pas présent dans le DTO de réponse.
     */
    @Mapping(target = "idRole", source = "role.idRole")
    @Mapping(target = "idUnite", source = "unite.idUnite")
    @Mapping(target = "matricule", source = "matricule")
    UtilisateurResponseDTO toResponseDTO(Utilisateur entity);

    /**
     * Conversion du UtilisateurRequestDTO vers une nouvelle entité Utilisateur.
     * La clé primaire idUser est ignorée.
     * Les relations Role et Unite sont résolues via les méthodes helper.
     */
    @Mapping(target = "idUser", ignore = true)
    @Mapping(target = "matricule", source = "matricule")
    @Mapping(target = "role", source = "idRole")
    @Mapping(target = "unite", source = "idUnite")
    Utilisateur toEntity(UtilisateurRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Utilisateur existante à partir d'un RequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idUser", ignore = true)
    @Mapping(target = "matricule", source = "matricule")
    @Mapping(target = "role", source = "idRole")
    @Mapping(target = "unite", source = "idUnite")
    void updateEntityFromDto(UtilisateurRequestDTO dto, @MappingTarget Utilisateur entity);

    /**
     * Convertit un ID de rôle en une référence d'entité Role.
     */
    default Role mapRole(Long id) {
        if (id == null) {
            return null;
        }
        Role role = new Role();
        role.setIdRole(id);
        return role;
    }

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
}
