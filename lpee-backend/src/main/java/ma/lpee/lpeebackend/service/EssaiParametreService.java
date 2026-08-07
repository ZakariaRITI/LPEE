package ma.lpee.lpeebackend.service;

import ma.lpee.lpeebackend.dto.request.EssaiParametreRequestDTO;
import ma.lpee.lpeebackend.dto.response.EssaiParametreResponseDTO;

import java.util.List;

public interface EssaiParametreService {

    EssaiParametreResponseDTO create(EssaiParametreRequestDTO requestDTO);

    EssaiParametreResponseDTO update(Long id, EssaiParametreRequestDTO requestDTO);

    EssaiParametreResponseDTO getById(Long id);

    List<EssaiParametreResponseDTO> getAll();

    List<EssaiParametreResponseDTO> getByEssai(Long idEssai);

    List<EssaiParametreResponseDTO> getByParametre(Long idParametre);

    void delete(Long id);
}