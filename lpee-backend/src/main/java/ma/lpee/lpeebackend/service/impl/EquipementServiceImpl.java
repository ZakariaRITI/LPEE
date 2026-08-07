package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.EquipementRequestDTO;
import ma.lpee.lpeebackend.dto.response.EquipementResponseDTO;
import ma.lpee.lpeebackend.entity.Equipement;
import ma.lpee.lpeebackend.entity.Marque;
import ma.lpee.lpeebackend.mapper.EquipementMapper;
import ma.lpee.lpeebackend.repository.EquipementRepository;
import ma.lpee.lpeebackend.repository.MarqueRepository;
import ma.lpee.lpeebackend.service.EquipementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipementServiceImpl implements EquipementService {

    private final EquipementRepository equipementRepository;
    private final MarqueRepository marqueRepository;
    private final EquipementMapper equipementMapper;

    @Override
    public EquipementResponseDTO create(EquipementRequestDTO requestDTO) {

        Marque marque = marqueRepository.findById(requestDTO.getIdMarque())
                .orElseThrow(() -> new RuntimeException("Marque introuvable."));

        if (equipementRepository.existsByNumeroSerie(requestDTO.getNumeroSerie())) {
            throw new RuntimeException("Un équipement avec ce numéro de série existe déjà.");
        }

        Equipement equipement = equipementMapper.toEntity(requestDTO);
        equipement.setMarque(marque);

        Equipement saved = equipementRepository.save(equipement);

        return equipementMapper.toResponseDTO(saved);
    }

    @Override
    public EquipementResponseDTO update(Long id, EquipementRequestDTO requestDTO) {

        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Équipement introuvable."));

        Marque marque = marqueRepository.findById(requestDTO.getIdMarque())
                .orElseThrow(() -> new RuntimeException("Marque introuvable."));

        if (!equipement.getNumeroSerie().equals(requestDTO.getNumeroSerie())
                && equipementRepository.existsByNumeroSerie(requestDTO.getNumeroSerie())) {
            throw new RuntimeException("Un équipement avec ce numéro de série existe déjà.");
        }

        equipementMapper.updateEntityFromDto(requestDTO, equipement);
        equipement.setMarque(marque);

        Equipement updated = equipementRepository.save(equipement);

        return equipementMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipementResponseDTO getById(Long id) {

        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Équipement introuvable."));

        return equipementMapper.toResponseDTO(equipement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipementResponseDTO> getAll() {

        return equipementRepository.findAll()
                .stream()
                .map(equipementMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipementResponseDTO> getByMarque(Long idMarque) {

        return equipementRepository.findByMarqueIdMarque(idMarque)
                .stream()
                .map(equipementMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Équipement introuvable."));

        equipementRepository.delete(equipement);
    }
}