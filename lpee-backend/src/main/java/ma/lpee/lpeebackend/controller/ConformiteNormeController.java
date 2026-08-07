package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.ConformiteNormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.ConformiteNormeResponseDTO;
import ma.lpee.lpeebackend.service.ConformiteNormeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conformites-normes")
@RequiredArgsConstructor
public class ConformiteNormeController {

    private final ConformiteNormeService conformiteNormeService;

    @PostMapping
    public ResponseEntity<ConformiteNormeResponseDTO> create(
            @Valid @RequestBody ConformiteNormeRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(conformiteNormeService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConformiteNormeResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ConformiteNormeRequestDTO requestDTO) {

        return ResponseEntity.ok(
                conformiteNormeService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConformiteNormeResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                conformiteNormeService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<ConformiteNormeResponseDTO>> getAll() {

        return ResponseEntity.ok(
                conformiteNormeService.getAll()
        );
    }

    @GetMapping("/essai/{idEssai}")
    public ResponseEntity<List<ConformiteNormeResponseDTO>> getByEssai(
            @PathVariable Long idEssai) {

        return ResponseEntity.ok(
                conformiteNormeService.getByEssai(idEssai)
        );
    }

    @GetMapping("/norme/{idNorme}")
    public ResponseEntity<List<ConformiteNormeResponseDTO>> getByNorme(
            @PathVariable Long idNorme) {

        return ResponseEntity.ok(
                conformiteNormeService.getByNorme(idNorme)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        conformiteNormeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
