package ma.lpee.lpeebackend.controller;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.UtilisateurRequestDTO;
import ma.lpee.lpeebackend.dto.response.UtilisateurResponseDTO;
import ma.lpee.lpeebackend.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;


    @PostMapping
    public ResponseEntity<UtilisateurResponseDTO> create(
            @RequestBody UtilisateurRequestDTO dto) {

        return new ResponseEntity<>(
                utilisateurService.create(dto),
                HttpStatus.CREATED
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                utilisateurService.getById(id)
        );
    }


    @GetMapping
    public ResponseEntity<List<UtilisateurResponseDTO>> getAll() {

        return ResponseEntity.ok(
                utilisateurService.getAll()
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurResponseDTO> update(
            @PathVariable Long id,
            @RequestBody UtilisateurRequestDTO dto) {

        return ResponseEntity.ok(
                utilisateurService.update(id, dto)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        utilisateurService.delete(id);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/unite/{idUnite}")
    public ResponseEntity<List<UtilisateurResponseDTO>> getByUnite(
            @PathVariable Long idUnite) {

        return ResponseEntity.ok(
                utilisateurService.getByUnite(idUnite)
        );
    }


    @GetMapping("/role/{idRole}")
    public ResponseEntity<List<UtilisateurResponseDTO>> getByRole(
            @PathVariable Long idRole) {

        return ResponseEntity.ok(
                utilisateurService.getByRole(idRole)
        );
    }


    @GetMapping("/email/{email}")
    public ResponseEntity<UtilisateurResponseDTO> getByEmail(
            @PathVariable String email) {

        return ResponseEntity.ok(
                utilisateurService.getByEmail(email)
        );
    }
}