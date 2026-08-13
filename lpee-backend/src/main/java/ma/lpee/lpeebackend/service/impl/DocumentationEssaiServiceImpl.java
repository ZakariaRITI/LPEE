package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.DocumentationEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.DocumentationEssaiResponseDTO;
import ma.lpee.lpeebackend.entity.Document;
import ma.lpee.lpeebackend.entity.DocumentationEssai;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.DocumentationEssaiMapper;
import ma.lpee.lpeebackend.repository.DocumentRepository;
import ma.lpee.lpeebackend.repository.DocumentationEssaiRepository;
import ma.lpee.lpeebackend.repository.EssaiRepository;
import ma.lpee.lpeebackend.service.DocumentationEssaiService;
import ma.lpee.lpeebackend.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentationEssaiServiceImpl implements DocumentationEssaiService {

    private final DocumentationEssaiRepository documentationEssaiRepository;
    private final EssaiRepository essaiRepository;
    private final DocumentRepository documentRepository;
    private final DocumentationEssaiMapper documentationEssaiMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public DocumentationEssaiResponseDTO create(DocumentationEssaiRequestDTO requestDTO) {

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new ResourceNotFoundException("Essai introuvable."));

        Document document = documentRepository.findById(requestDTO.getIdDocument())
                .orElseThrow(() -> new ResourceNotFoundException("Document introuvable."));

        if (documentationEssaiRepository.existsByEssaiIdEssaiAndDocumentIdDocument(
                requestDTO.getIdEssai(),
                requestDTO.getIdDocument())) {
            throw new DuplicateResourceException("Cette documentation d'essai existe déjà.");
        }

        DocumentationEssai documentationEssai = documentationEssaiMapper.toEntity(requestDTO);

        documentationEssai.setEssai(essai);
        documentationEssai.setDocument(document);
        documentationEssai.setCreeLe(LocalDateTime.now());

        DocumentationEssai saved = documentationEssaiRepository.save(documentationEssai);

        return documentationEssaiMapper.toResponseDTO(saved);
    }

    @Override
    public DocumentationEssaiResponseDTO update(Long id, DocumentationEssaiRequestDTO requestDTO) {

        DocumentationEssai documentationEssai = documentationEssaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documentation d'essai introuvable."));

        Essai essai = essaiRepository.findById(requestDTO.getIdEssai())
                .orElseThrow(() -> new ResourceNotFoundException("Essai introuvable."));

        Document document = documentRepository.findById(requestDTO.getIdDocument())
                .orElseThrow(() -> new ResourceNotFoundException("Document introuvable."));

        documentationEssaiMapper.updateEntityFromDto(requestDTO, documentationEssai);

        documentationEssai.setEssai(essai);
        documentationEssai.setDocument(document);
        Long authenticatedUserId = authenticatedUserService.getAuthenticatedUserId();
        documentationEssai.setModifieLe(LocalDateTime.now());
        documentationEssai.setModifiePar(authenticatedUserId);
        if ("INACTIF".equalsIgnoreCase(documentationEssai.getStatut())) {
            documentationEssai.setAnnuleLe(LocalDateTime.now());
            documentationEssai.setAnnulePar(authenticatedUserId);
        }

        DocumentationEssai updated = documentationEssaiRepository.save(documentationEssai);

        return documentationEssaiMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentationEssaiResponseDTO getById(Long id) {

        DocumentationEssai documentationEssai = documentationEssaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documentation d'essai introuvable."));

        return documentationEssaiMapper.toResponseDTO(documentationEssai);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentationEssaiResponseDTO> getAll() {

        return documentationEssaiRepository.findAll()
                .stream()
                .map(documentationEssaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentationEssaiResponseDTO> getByEssai(Long idEssai) {

        return documentationEssaiRepository.findByEssaiIdEssai(idEssai)
                .stream()
                .map(documentationEssaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentationEssaiResponseDTO> getByDocument(Long idDocument) {

        return documentationEssaiRepository.findByDocumentIdDocument(idDocument)
                .stream()
                .map(documentationEssaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        DocumentationEssai documentationEssai = documentationEssaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documentation d'essai introuvable."));

        documentationEssai.setStatut("INACTIF");
        documentationEssai.setAnnuleLe(LocalDateTime.now());
        documentationEssai.setAnnulePar(authenticatedUserService.getAuthenticatedUserId());
        documentationEssaiRepository.save(documentationEssai);
    }
}
