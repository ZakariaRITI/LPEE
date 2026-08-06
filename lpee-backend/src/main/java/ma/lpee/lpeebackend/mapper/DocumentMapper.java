package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.DocumentRequestDTO;
import ma.lpee.lpeebackend.dto.response.DocumentResponseDTO;
import ma.lpee.lpeebackend.entity.Document;
import ma.lpee.lpeebackend.entity.TypeDocument;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    /**
     * Conversion de l'entité Document vers DocumentResponseDTO.
     * Mappe la relation typeDocument vers l'identifiant idType.
     * La collection normative (@ManyToMany) est absente du DTO de réponse, elle est donc omise.
     */
    @Mapping(target = "idType", source = "typeDocument.idType")
    DocumentResponseDTO toResponseDTO(Document entity);

    /**
     * Conversion du DocumentRequestDTO vers une nouvelle entité Document.
     * La clé primaire idDocument et la collection normes sont ignorées.
     * La relation typeDocument est résolue via la méthode mapTypeDocument.
     */
    @Mapping(target = "idDocument", ignore = true)
    @Mapping(target = "typeDocument", source = "idType")
    @Mapping(target = "normes", ignore = true)
    Document toEntity(DocumentRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité Document existante à partir d'un DocumentRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idDocument", ignore = true)
    @Mapping(target = "typeDocument", source = "idType")
    @Mapping(target = "normes", ignore = true)
    void updateEntityFromDto(DocumentRequestDTO dto, @MappingTarget Document entity);

    /**
     * Convertit un ID de type de document en une référence d'entité TypeDocument.
     */
    default TypeDocument mapTypeDocument(Long id) {
        if (id == null) {
            return null;
        }
        TypeDocument typeDocument = new TypeDocument();
        typeDocument.setIdType(id);
        return typeDocument;
    }
}