package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.DocumentRequestDTO;
import ma.lpee.lpeebackend.dto.response.DocumentResponseDTO;
import ma.lpee.lpeebackend.entity.Document;
import ma.lpee.lpeebackend.entity.TypeDocument;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.DocumentMapper;
import ma.lpee.lpeebackend.repository.DocumentRepository;
import ma.lpee.lpeebackend.repository.TypeDocumentRepository;
import ma.lpee.lpeebackend.service.DocumentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final TypeDocumentRepository typeDocumentRepository;
    private final DocumentMapper documentMapper;

    @Override
    public DocumentResponseDTO create(DocumentRequestDTO requestDTO) {

        TypeDocument typeDocument = typeDocumentRepository.findById(requestDTO.getIdType())
                .orElseThrow(() -> new ResourceNotFoundException("Type de document introuvable."));

        if (documentRepository.existsByNumeroDocument(requestDTO.getNumeroDocument())) {
            throw new DuplicateResourceException("Un document avec ce numéro existe déjà.");
        }

        Document document = documentMapper.toEntity(requestDTO);
        document.setTypeDocument(typeDocument);

        Document saved = documentRepository.save(document);

        return documentMapper.toResponseDTO(saved);
    }

    @Override
    public DocumentResponseDTO update(Long id, DocumentRequestDTO requestDTO) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document introuvable."));

        TypeDocument typeDocument = typeDocumentRepository.findById(requestDTO.getIdType())
                .orElseThrow(() -> new ResourceNotFoundException("Type de document introuvable."));

        documentRepository.findByNumeroDocument(requestDTO.getNumeroDocument())
                .ifPresent(existingDocument -> {
                    if (!existingDocument.getIdDocument().equals(id)) {
                        throw new DuplicateResourceException("Un document avec ce numéro existe déjà.");
                    }
                });

        documentMapper.updateEntityFromDto(requestDTO, document);
        document.setTypeDocument(typeDocument);

        Document updated = documentRepository.save(document);

        return documentMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponseDTO getById(Long id) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document introuvable."));

        return documentMapper.toResponseDTO(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getAll() {

        return documentRepository.findAll()
                .stream()
                .map(documentMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponseDTO> getByType(Long idType) {

        return documentRepository.findByTypeDocumentIdType(idType)
                .stream()
                .map(documentMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document introuvable."));

        documentRepository.delete(document);
    }
}