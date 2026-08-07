package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.TypeDocumentRequestDTO;
import ma.lpee.lpeebackend.dto.response.TypeDocumentResponseDTO;

import java.util.List;

public interface TypeDocumentService {

    TypeDocumentResponseDTO create(TypeDocumentRequestDTO dto);

    TypeDocumentResponseDTO findById(Long id);

    List<TypeDocumentResponseDTO> findAll();

    TypeDocumentResponseDTO update(Long id, TypeDocumentRequestDTO dto);

    void delete(Long id);

    TypeDocumentResponseDTO findByNomType(String nomType);
}