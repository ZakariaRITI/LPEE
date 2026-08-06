package ma.lpee.lpeebackend.mapper;

import ma.lpee.lpeebackend.dto.request.TypeDocumentRequestDTO;
import ma.lpee.lpeebackend.dto.response.TypeDocumentResponseDTO;
import ma.lpee.lpeebackend.entity.TypeDocument;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TypeDocumentMapper {

    /**
     * Conversion de l'entité TypeDocument vers TypeDocumentResponseDTO.
     */
    TypeDocumentResponseDTO toResponseDTO(TypeDocument entity);

    /**
     * Conversion du TypeDocumentRequestDTO vers une nouvelle entité TypeDocument.
     * La clé primaire idType est ignorée.
     */
    @Mapping(target = "idType", ignore = true)
    TypeDocument toEntity(TypeDocumentRequestDTO dto);

    /**
     * Mise à jour partielle d'une entité TypeDocument existante à partir d'un TypeDocumentRequestDTO.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idType", ignore = true)
    void updateEntityFromDto(TypeDocumentRequestDTO dto, @MappingTarget TypeDocument entity);
}