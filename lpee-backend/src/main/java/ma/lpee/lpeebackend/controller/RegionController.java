package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.RegionRequestDTO;
import ma.lpee.lpeebackend.dto.response.RegionResponseDTO;
import ma.lpee.lpeebackend.service.RegionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @PostMapping
    public ResponseEntity<RegionResponseDTO> create(
            @Valid @RequestBody RegionRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(regionService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegionResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                regionService.findById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<RegionResponseDTO>> findAll() {

        return ResponseEntity.ok(
                regionService.findAll()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RegionRequestDTO dto) {

        return ResponseEntity.ok(
                regionService.update(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        regionService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/code/{codeRegion}")
    public ResponseEntity<RegionResponseDTO> findByCodeRegion(
            @PathVariable String codeRegion) {

        return ResponseEntity.ok(
                regionService.findByCodeRegion(codeRegion)
        );
    }
}