package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.DocumentationEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.DocumentationEssaiResponseDTO;
import ma.lpee.lpeebackend.entity.Document;
import ma.lpee.lpeebackend.entity.DocumentationEssai;
import ma.lpee.lpeebackend.entity.Essai;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DocumentationEssaiMapper {

    /**
     * Conversion de l'entité DocumentationEssai vers DocumentationEssaiResponseDTO.
     * Mappe les objets imbriqués Essai et Document vers leurs identifiants simples.
     */
    @Mapping(target = "idEssai", source = "essai.idEssai")
    @Mapping(target = "idDocument", source = "document.idDocument")
    DocumentationEssaiResponseDTO toResponseDTO(DocumentationEssai entity);

    /**
     * Conversion du DocumentationEssaiRequestDTO vers une nouvelle entité.
     * La clé primaire idDocumentationEssai et les champs d'audit système sont ignorés.
     * Les relations Essai et Document sont résolues via les méthodes helper.
     */
    @Mapping(target = "idDocumentationEssai", ignore = true)
    @Mapping(target = "essai", source = "idEssai")
    @Mapping(target = "document", source = "idDocument")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    DocumentationEssai toEntity(DocumentationEssaiRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité DocumentationEssai existante à partir d'un RequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idDocumentationEssai", ignore = true)
    @Mapping(target = "essai", source = "idEssai")
    @Mapping(target = "document", source = "idDocument")
    @Mapping(target = "creeLe", ignore = true)
    @Mapping(target = "creePar", ignore = true)
    @Mapping(target = "modifieLe", ignore = true)
    @Mapping(target = "modifiePar", ignore = true)
    @Mapping(target = "annuleLe", ignore = true)
    @Mapping(target = "annulePar", ignore = true)
    void updateEntityFromDto(DocumentationEssaiRequestDTO dto, @MappingTarget DocumentationEssai entity);

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
     * Convertit un ID de document en une référence d'entité Document.
     */
    default Document mapDocument(Long id) {
        if (id == null) {
            return null;
        }
        Document document = new Document();
        document.setIdDocument(id);
        return document;
    }
}