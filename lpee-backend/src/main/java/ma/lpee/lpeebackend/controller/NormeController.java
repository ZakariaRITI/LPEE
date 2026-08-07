package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.NormeRequestDTO;
import ma.lpee.lpeebackend.dto.response.NormeResponseDTO;
import ma.lpee.lpeebackend.service.NormeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/normes")
@RequiredArgsConstructor
public class NormeController {

    private final NormeService normeService;

    @PostMapping
    public ResponseEntity<NormeResponseDTO> create(
            @Valid @RequestBody NormeRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(normeService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NormeResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody NormeRequestDTO requestDTO) {

        return ResponseEntity.ok(
                normeService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<NormeResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                normeService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<NormeResponseDTO>> getAll() {

        return ResponseEntity.ok(
                normeService.getAll()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        normeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}