package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.OrganismeRequestDTO;
import ma.lpee.lpeebackend.dto.response.OrganismeResponseDTO;
import ma.lpee.lpeebackend.service.OrganismeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organismes")
@RequiredArgsConstructor
public class OrganismeController {

    private final OrganismeService organismeService;

    @PostMapping
    public ResponseEntity<OrganismeResponseDTO> create(
            @Valid @RequestBody OrganismeRequestDTO requestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(organismeService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganismeResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody OrganismeRequestDTO requestDTO) {

        return ResponseEntity.ok(
                organismeService.update(id, requestDTO)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganismeResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                organismeService.getById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<OrganismeResponseDTO>> getAll() {

        return ResponseEntity.ok(
                organismeService.getAll()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        organismeService.delete(id);

        return ResponseEntity.noContent().build();
    }
}