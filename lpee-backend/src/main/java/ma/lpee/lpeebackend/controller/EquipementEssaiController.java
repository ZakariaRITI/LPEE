package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.EquipementEssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.EquipementEssaiResponseDTO;
import ma.lpee.lpeebackend.service.EquipementEssaiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipements-essais")
@RequiredArgsConstructor
public class EquipementEssaiController {

    private final EquipementEssaiService equipementEssaiService;

    @PostMapping
    public ResponseEntity<EquipementEssaiResponseDTO> create(
            @Valid @RequestBody EquipementEssaiRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(equipementEssaiService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipementEssaiResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EquipementEssaiRequestDTO requestDTO) {

        return ResponseEntity.ok(
                equipementEssaiService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipementEssaiResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                equipementEssaiService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<EquipementEssaiResponseDTO>> getAll() {

        return ResponseEntity.ok(
                equipementEssaiService.getAll()
        );
    }

    @GetMapping("/essai/{idEssai}")
    public ResponseEntity<List<EquipementEssaiResponseDTO>> getByEssai(
            @PathVariable Long idEssai) {

        return ResponseEntity.ok(
                equipementEssaiService.getByEssai(idEssai)
        );
    }

    @GetMapping("/equipement/{idEquipement}")
    public ResponseEntity<List<EquipementEssaiResponseDTO>> getByEquipement(
            @PathVariable Long idEquipement) {

        return ResponseEntity.ok(
                equipementEssaiService.getByEquipement(idEquipement)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        equipementEssaiService.delete(id);

        return ResponseEntity.noContent().build();
    }
}

