package ma.lpee.lpeebackend.controller;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.TypeDocumentRequestDTO;
import ma.lpee.lpeebackend.dto.response.TypeDocumentResponseDTO;
import ma.lpee.lpeebackend.service.TypeDocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/type-documents")
@RequiredArgsConstructor
public class TypeDocumentController {

    private final TypeDocumentService typeDocumentService;


    @PostMapping
    public ResponseEntity<TypeDocumentResponseDTO> create(
            @RequestBody TypeDocumentRequestDTO dto) {

        return new ResponseEntity<>(
                typeDocumentService.create(dto),
                HttpStatus.CREATED
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<TypeDocumentResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                typeDocumentService.findById(id)
        );
    }


    @GetMapping
    public ResponseEntity<List<TypeDocumentResponseDTO>> findAll() {

        return ResponseEntity.ok(
                typeDocumentService.findAll()
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<TypeDocumentResponseDTO> update(
            @PathVariable Long id,
            @RequestBody TypeDocumentRequestDTO dto) {

        return ResponseEntity.ok(
                typeDocumentService.update(id, dto)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        typeDocumentService.delete(id);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/nom/{nomType}")
    public ResponseEntity<TypeDocumentResponseDTO> findByNomType(
            @PathVariable String nomType) {

        return ResponseEntity.ok(
                typeDocumentService.findByNomType(nomType)
        );
    }
}