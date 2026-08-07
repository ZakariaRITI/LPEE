package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.DocumentRequestDTO;
import ma.lpee.lpeebackend.dto.response.DocumentResponseDTO;
import ma.lpee.lpeebackend.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentResponseDTO> create(
            @Valid @RequestBody DocumentRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(documentService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DocumentRequestDTO requestDTO) {

        return ResponseEntity.ok(
                documentService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                documentService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponseDTO>> getAll() {

        return ResponseEntity.ok(
                documentService.getAll()
        );
    }

    @GetMapping("/type/{idType}")
    public ResponseEntity<List<DocumentResponseDTO>> getByType(
            @PathVariable Long idType) {

        return ResponseEntity.ok(
                documentService.getByType(idType)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        documentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
