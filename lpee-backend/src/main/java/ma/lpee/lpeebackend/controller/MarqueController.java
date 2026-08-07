package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.MarqueRequestDTO;
import ma.lpee.lpeebackend.dto.response.MarqueResponseDTO;
import ma.lpee.lpeebackend.service.MarqueService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marques")
@RequiredArgsConstructor
public class MarqueController {

    private final MarqueService marqueService;

    @PostMapping
    public ResponseEntity<MarqueResponseDTO> create(
            @Valid @RequestBody MarqueRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(marqueService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarqueResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody MarqueRequestDTO requestDTO) {

        return ResponseEntity.ok(
                marqueService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarqueResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                marqueService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<MarqueResponseDTO>> getAll() {

        return ResponseEntity.ok(
                marqueService.getAll()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        marqueService.delete(id);

        return ResponseEntity.noContent().build();
    }
}