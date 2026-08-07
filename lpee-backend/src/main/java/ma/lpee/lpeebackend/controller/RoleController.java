package ma.lpee.lpeebackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.RoleRequestDTO;
import ma.lpee.lpeebackend.dto.response.RoleResponseDTO;
import ma.lpee.lpeebackend.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponseDTO> create(
            @Valid @RequestBody RoleRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roleService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                roleService.findById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> findAll() {

        return ResponseEntity.ok(
                roleService.findAll()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDTO dto) {

        return ResponseEntity.ok(
                roleService.update(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        roleService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/code/{codeRole}")
    public ResponseEntity<RoleResponseDTO> findByCodeRole(
            @PathVariable String codeRole) {

        return ResponseEntity.ok(
                roleService.findByCodeRole(codeRole)
        );
    }
}