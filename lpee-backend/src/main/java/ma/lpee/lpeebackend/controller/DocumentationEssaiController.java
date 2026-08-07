package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.DocumentationEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.DocumentationEssaiResponseDTO;
import ma.lpee.lpeebackend.service.DocumentationEssaiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentations-essais")
@RequiredArgsConstructor
public class DocumentationEssaiController {

    private final DocumentationEssaiService documentationEssaiService;

    @PostMapping
    public ResponseEntity<DocumentationEssaiResponseDTO> create(
            @Valid @RequestBody DocumentationEssaiRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(documentationEssaiService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentationEssaiResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DocumentationEssaiRequestDTO requestDTO) {

        return ResponseEntity.ok(
                documentationEssaiService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentationEssaiResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                documentationEssaiService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<DocumentationEssaiResponseDTO>> getAll() {

        return ResponseEntity.ok(
                documentationEssaiService.getAll()
        );
    }

    @GetMapping("/essai/{idEssai}")
    public ResponseEntity<List<DocumentationEssaiResponseDTO>> getByEssai(
            @PathVariable Long idEssai) {

        return ResponseEntity.ok(
                documentationEssaiService.getByEssai(idEssai)
        );
    }

    @GetMapping("/document/{idDocument}")
    public ResponseEntity<List<DocumentationEssaiResponseDTO>> getByDocument(
            @PathVariable Long idDocument) {

        return ResponseEntity.ok(
                documentationEssaiService.getByDocument(idDocument)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        documentationEssaiService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
