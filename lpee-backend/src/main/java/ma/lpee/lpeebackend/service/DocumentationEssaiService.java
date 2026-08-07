package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.DocumentationEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.DocumentationEssaiResponseDTO;

import java.util.List;

public interface DocumentationEssaiService {

    DocumentationEssaiResponseDTO create(DocumentationEssaiRequestDTO requestDTO);

    DocumentationEssaiResponseDTO update(Long id, DocumentationEssaiRequestDTO requestDTO);

    DocumentationEssaiResponseDTO getById(Long id);

    List<DocumentationEssaiResponseDTO> getAll();

    List<DocumentationEssaiResponseDTO> getByEssai(Long idEssai);

    List<DocumentationEssaiResponseDTO> getByDocument(Long idDocument);

    void delete(Long id);
}