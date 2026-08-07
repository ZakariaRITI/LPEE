package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.ParametreRequestDTO;
import ma.lpee.lpeebackend.dto.response.ParametreResponseDTO;
import ma.lpee.lpeebackend.service.ParametreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parametres")
@RequiredArgsConstructor
public class ParametreController {

    private final ParametreService parametreService;

    @PostMapping
    public ResponseEntity<ParametreResponseDTO> create(
            @Valid @RequestBody ParametreRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(parametreService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParametreResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ParametreRequestDTO requestDTO) {

        return ResponseEntity.ok(
                parametreService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParametreResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                parametreService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<ParametreResponseDTO>> getAll() {

        return ResponseEntity.ok(
                parametreService.getAll()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        parametreService.delete(id);

        return ResponseEntity.noContent().build();
    }
}