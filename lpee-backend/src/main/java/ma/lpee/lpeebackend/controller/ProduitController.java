package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.ProduitRequestDTO;
import ma.lpee.lpeebackend.dto.response.ProduitResponseDTO;
import ma.lpee.lpeebackend.service.ProduitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    @PostMapping
    public ResponseEntity<ProduitResponseDTO> create(
            @Valid @RequestBody ProduitRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(produitService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProduitResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProduitRequestDTO requestDTO) {

        return ResponseEntity.ok(
                produitService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                produitService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<ProduitResponseDTO>> getAll() {

        return ResponseEntity.ok(
                produitService.getAll()
        );
    }

    @GetMapping("/famille/{idFamille}")
    public ResponseEntity<List<ProduitResponseDTO>> getByFamille(
            @PathVariable Long idFamille) {

        return ResponseEntity.ok(
                produitService.getByFamille(idFamille)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        produitService.delete(id);

        return ResponseEntity.noContent().build();
    }
}