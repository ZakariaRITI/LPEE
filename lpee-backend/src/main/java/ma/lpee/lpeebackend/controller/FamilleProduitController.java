package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.FamilleProduitRequestDTO;
import ma.lpee.lpeebackend.dto.response.FamilleProduitResponseDTO;
import ma.lpee.lpeebackend.service.FamilleProduitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/familles-produits")
@RequiredArgsConstructor
public class FamilleProduitController {

    private final FamilleProduitService familleProduitService;

    @PostMapping
    public ResponseEntity<FamilleProduitResponseDTO> create(
            @Valid @RequestBody FamilleProduitRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(familleProduitService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FamilleProduitResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody FamilleProduitRequestDTO requestDTO) {

        return ResponseEntity.ok(
                familleProduitService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilleProduitResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                familleProduitService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<FamilleProduitResponseDTO>> getAll() {

        return ResponseEntity.ok(
                familleProduitService.getAll()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        familleProduitService.delete(id);

        return ResponseEntity.noContent().build();
    }
}