package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.TypeDocumentRequestDTO;
import ma.lpee.lpeebackend.dto.response.TypeDocumentResponseDTO;
import ma.lpee.lpeebackend.entity.TypeDocument;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.TypeDocumentMapper;
import ma.lpee.lpeebackend.repository.TypeDocumentRepository;
import ma.lpee.lpeebackend.service.TypeDocumentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TypeDocumentServiceImpl implements TypeDocumentService {

    private final TypeDocumentRepository typeDocumentRepository;
    private final TypeDocumentMapper typeDocumentMapper;

    @Override
    public TypeDocumentResponseDTO create(TypeDocumentRequestDTO dto) {

        TypeDocument entity = typeDocumentMapper.toEntity(dto);

        TypeDocument savedEntity = typeDocumentRepository.save(entity);

        return typeDocumentMapper.toResponseDTO(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public TypeDocumentResponseDTO findById(Long id) {

        TypeDocument entity = typeDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Type de document introuvable avec l'id : " + id
                ));

        return typeDocumentMapper.toResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeDocumentResponseDTO> findAll() {

        return typeDocumentRepository.findAll()
                .stream()
                .map(typeDocumentMapper::toResponseDTO)
                .toList();
    }

    @Override
    public TypeDocumentResponseDTO update(
            Long id,
            TypeDocumentRequestDTO dto) {

        TypeDocument entity = typeDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Type de document introuvable avec l'id : " + id
                ));

        typeDocumentMapper.updateEntityFromDto(dto, entity);

        TypeDocument updatedEntity =
                typeDocumentRepository.save(entity);

        return typeDocumentMapper.toResponseDTO(updatedEntity);
    }

    @Override
    public void delete(Long id) {

        TypeDocument entity = typeDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Type de document introuvable avec l'id : " + id
                ));

        typeDocumentRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public TypeDocumentResponseDTO findByNomType(String nomType) {

        TypeDocument entity = typeDocumentRepository.findByNomType(nomType)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Type de document introuvable avec le nom : " + nomType
                ));

        return typeDocumentMapper.toResponseDTO(entity);
    }
}