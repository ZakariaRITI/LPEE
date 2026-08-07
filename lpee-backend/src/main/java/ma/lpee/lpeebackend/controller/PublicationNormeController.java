package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.PublicationNormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.PublicationNormeResponseDTO;
import ma.lpee.lpeebackend.service.PublicationNormeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publications-normes")
@RequiredArgsConstructor
public class PublicationNormeController {

    private final PublicationNormeService publicationNormeService;

    @PostMapping
    public ResponseEntity<PublicationNormeResponseDTO> create(
            @Valid @RequestBody PublicationNormeRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(publicationNormeService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationNormeResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                publicationNormeService.findById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<PublicationNormeResponseDTO>> findAll() {

        return ResponseEntity.ok(
                publicationNormeService.findAll()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublicationNormeResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PublicationNormeRequestDTO dto) {

        return ResponseEntity.ok(
                publicationNormeService.update(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        publicationNormeService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/norme/{idNorme}")
    public ResponseEntity<List<PublicationNormeResponseDTO>> findByNorme(
            @PathVariable Long idNorme) {

        return ResponseEntity.ok(
                publicationNormeService.findByNorme(idNorme)
        );
    }

    @GetMapping("/organisme/{idOrganisme}")
    public ResponseEntity<List<PublicationNormeResponseDTO>> findByOrganisme(
            @PathVariable Long idOrganisme) {

        return ResponseEntity.ok(
                publicationNormeService.findByOrganisme(idOrganisme)
        );
    }
}