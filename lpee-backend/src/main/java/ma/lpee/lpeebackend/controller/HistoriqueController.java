package ma.lpee.lpeebackend.controller;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.response.HistoriqueActionResponseDTO;
import ma.lpee.lpeebackend.service.HistoriqueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historique")
@RequiredArgsConstructor
public class HistoriqueController {

    private final HistoriqueService historiqueService;

    @GetMapping
    public ResponseEntity<List<HistoriqueActionResponseDTO>> findAll() {
        return ResponseEntity.ok(historiqueService.findAll());
    }
}
