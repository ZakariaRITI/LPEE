package ma.lpee.lpeebackend.controller;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.UniteRequestDTO;
import ma.lpee.lpeebackend.dto.response.UniteResponseDTO;
import ma.lpee.lpeebackend.service.UniteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/unites")
@RequiredArgsConstructor
public class UniteController {

    private final UniteService uniteService;


    @PostMapping
    public ResponseEntity<UniteResponseDTO> create(
            @Valid @RequestBody UniteRequestDTO dto) {

        return new ResponseEntity<>(
                uniteService.create(dto),
                HttpStatus.CREATED
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<UniteResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                uniteService.getById(id)
        );
    }


    @GetMapping(params = {"!page", "!size"})
    public ResponseEntity<List<UniteResponseDTO>> getAll() {

        return ResponseEntity.ok(
                uniteService.getAll()
        );
    }

    @GetMapping(params = {"page", "size"})
    public ResponseEntity<Page<UniteResponseDTO>> getPage(
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "") String codeUnite) {

        return ResponseEntity.ok(
                uniteService.getPage(
                        PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 5), pageable.getSort()),
                        codeUnite
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<UniteResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UniteRequestDTO dto) {

        return ResponseEntity.ok(
                uniteService.update(id, dto)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        uniteService.delete(id);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/region/{idRegion}")
    public ResponseEntity<List<UniteResponseDTO>> getByRegion(
            @PathVariable Long idRegion) {

        return ResponseEntity.ok(
                uniteService.getByRegion(idRegion)
        );
    }


    @GetMapping("/code/{codeUnite}")
    public ResponseEntity<UniteResponseDTO> getByCodeUnite(
            @PathVariable String codeUnite) {

        return ResponseEntity.ok(
                uniteService.getByCodeUnite(codeUnite)
        );
    }
}
