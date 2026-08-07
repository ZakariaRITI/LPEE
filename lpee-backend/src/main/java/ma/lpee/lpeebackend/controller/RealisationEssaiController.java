package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.RealisationEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.RealisationEssaiResponseDTO;
import ma.lpee.lpeebackend.service.RealisationEssaiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/realisations-essais")
@RequiredArgsConstructor
public class RealisationEssaiController {

    private final RealisationEssaiService realisationEssaiService;

    @PostMapping
    public ResponseEntity<RealisationEssaiResponseDTO> create(
            @Valid @RequestBody RealisationEssaiRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(realisationEssaiService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RealisationEssaiResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                realisationEssaiService.findById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<RealisationEssaiResponseDTO>> findAll() {

        return ResponseEntity.ok(
                realisationEssaiService.findAll()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RealisationEssaiResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RealisationEssaiRequestDTO dto) {

        return ResponseEntity.ok(
                realisationEssaiService.update(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        realisationEssaiService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/essai/{idEssai}")
    public ResponseEntity<List<RealisationEssaiResponseDTO>> findByEssai(
            @PathVariable Long idEssai) {

        return ResponseEntity.ok(
                realisationEssaiService.findByEssai(idEssai)
        );
    }

    @GetMapping("/unite/{idUnite}")
    public ResponseEntity<List<RealisationEssaiResponseDTO>> findByUnite(
            @PathVariable Long idUnite) {

        return ResponseEntity.ok(
                realisationEssaiService.findByUnite(idUnite)
        );
    }

    @GetMapping("/cree-par/{idUser}")
    public ResponseEntity<List<RealisationEssaiResponseDTO>> findByCreePar(
            @PathVariable Long idUser) {

        return ResponseEntity.ok(
                realisationEssaiService.findByCreePar(idUser)
        );
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<RealisationEssaiResponseDTO>> findByStatut(
            @PathVariable String statut) {

        return ResponseEntity.ok(
                realisationEssaiService.findByStatut(statut)
        );
    }
}