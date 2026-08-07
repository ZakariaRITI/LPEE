package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.EssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.EssaiResponseDTO;
import ma.lpee.lpeebackend.service.EssaiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/essais")
@RequiredArgsConstructor
public class EssaiController {

    private final EssaiService essaiService;

    @PostMapping
    public ResponseEntity<EssaiResponseDTO> create(
            @Valid @RequestBody EssaiRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(essaiService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EssaiResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EssaiRequestDTO requestDTO) {

        return ResponseEntity.ok(
                essaiService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EssaiResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                essaiService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<EssaiResponseDTO>> getAll() {

        return ResponseEntity.ok(
                essaiService.getAll()
        );
    }

    @GetMapping("/produit/{idProduit}")
    public ResponseEntity<List<EssaiResponseDTO>> getByProduit(
            @PathVariable Long idProduit) {

        return ResponseEntity.ok(
                essaiService.getByProduit(idProduit)
        );
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<EssaiResponseDTO>> getByStatut(
            @PathVariable String statut) {

        return ResponseEntity.ok(
                essaiService.getByStatut(statut)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        essaiService.delete(id);

        return ResponseEntity.noContent().build();
    }
}