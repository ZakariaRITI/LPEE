package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.EquipementRequestDTO;
import ma.lpee.lpeebackend.dto.response.EquipementResponseDTO;
import ma.lpee.lpeebackend.service.EquipementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipements")
@RequiredArgsConstructor
public class EquipementController {

    private final EquipementService equipementService;

    @PostMapping
    public ResponseEntity<EquipementResponseDTO> create(
            @Valid @RequestBody EquipementRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(equipementService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipementResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EquipementRequestDTO requestDTO) {

        return ResponseEntity.ok(
                equipementService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipementResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                equipementService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<EquipementResponseDTO>> getAll() {

        return ResponseEntity.ok(
                equipementService.getAll()
        );
    }

    @GetMapping("/marque/{idMarque}")
    public ResponseEntity<List<EquipementResponseDTO>> getByMarque(
            @PathVariable Long idMarque) {

        return ResponseEntity.ok(
                equipementService.getByMarque(idMarque)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        equipementService.delete(id);

        return ResponseEntity.noContent().build();
    }
}