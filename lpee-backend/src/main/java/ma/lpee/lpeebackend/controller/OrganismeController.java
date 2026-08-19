package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.OrganismeRequestDTO;
import ma.lpee.lpeebackend.dto.response.OrganismeResponseDTO;
import ma.lpee.lpeebackend.service.OrganismeService;
import ma.lpee.lpeebackend.service.OrganismeImageStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organismes")
@RequiredArgsConstructor
public class OrganismeController {

    private final OrganismeService organismeService;
    private final OrganismeImageStorageService organismeImageStorageService;

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        String filename = organismeImageStorageService.store(file);
        return ResponseEntity.ok(Map.of(
                "imageOrganisme", "/api/organismes/images/" + filename
        ));
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource resource = organismeImageStorageService.load(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(organismeImageStorageService.contentType(filename)))
                .body(resource);
    }

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
