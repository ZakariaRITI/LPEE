package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.UniteRequestDTO;
import ma.lpee.lpeebackend.dto.response.UniteResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UniteService {

    UniteResponseDTO create(UniteRequestDTO dto);

    UniteResponseDTO getById(Long id);

    List<UniteResponseDTO> getAll();

    Page<UniteResponseDTO> getPage(Pageable pageable, String codeUnite);

    UniteResponseDTO update(Long id, UniteRequestDTO dto);

    void delete(Long id);

    List<UniteResponseDTO> getByRegion(Long idRegion);

    UniteResponseDTO getByCodeUnite(String codeUnite);
}

