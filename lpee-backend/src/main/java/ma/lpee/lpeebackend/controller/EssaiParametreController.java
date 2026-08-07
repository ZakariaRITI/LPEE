package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.EssaiParametreRequestDTO;
import ma.lpee.lpeebackend.dto.response.EssaiParametreResponseDTO;
import ma.lpee.lpeebackend.service.EssaiParametreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/essais-parametres")
@RequiredArgsConstructor
public class EssaiParametreController {

    private final EssaiParametreService essaiParametreService;

    @PostMapping
    public ResponseEntity<EssaiParametreResponseDTO> create(
            @Valid @RequestBody EssaiParametreRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(essaiParametreService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EssaiParametreResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EssaiParametreRequestDTO requestDTO) {

        return ResponseEntity.ok(
                essaiParametreService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EssaiParametreResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                essaiParametreService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<EssaiParametreResponseDTO>> getAll() {

        return ResponseEntity.ok(
                essaiParametreService.getAll()
        );
    }

    @GetMapping("/essai/{idEssai}")
    public ResponseEntity<List<EssaiParametreResponseDTO>> getByEssai(
            @PathVariable Long idEssai) {

        return ResponseEntity.ok(
                essaiParametreService.getByEssai(idEssai)
        );
    }

    @GetMapping("/parametre/{idParametre}")
    public ResponseEntity<List<EssaiParametreResponseDTO>> getByParametre(
            @PathVariable Long idParametre) {

        return ResponseEntity.ok(
                essaiParametreService.getByParametre(idParametre)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        essaiParametreService.delete(id);

        return ResponseEntity.noContent().build();
    }
}