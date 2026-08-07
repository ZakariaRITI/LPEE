package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.DocumentRequestDTO;
import ma.lpee.lpeebackend.dto.response.DocumentResponseDTO;

import java.util.List;

public interface DocumentService {

    DocumentResponseDTO create(DocumentRequestDTO requestDTO);

    DocumentResponseDTO update(Long id, DocumentRequestDTO requestDTO);

    DocumentResponseDTO getById(Long id);

    List<DocumentResponseDTO> getAll();

    List<DocumentResponseDTO> getByType(Long idType);

    void delete(Long id);
}