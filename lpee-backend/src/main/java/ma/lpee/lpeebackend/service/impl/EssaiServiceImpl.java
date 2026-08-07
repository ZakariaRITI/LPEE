package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.EssaiRequestDTO;
import ma.lpee.lpeebackend.dto.response.EssaiResponseDTO;
import ma.lpee.lpeebackend.entity.Essai;
import ma.lpee.lpeebackend.entity.Produit;
import ma.lpee.lpeebackend.mapper.EssaiMapper;
import ma.lpee.lpeebackend.repository.EssaiRepository;
import ma.lpee.lpeebackend.repository.ProduitRepository;
import ma.lpee.lpeebackend.service.EssaiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EssaiServiceImpl implements EssaiService {

    private final EssaiRepository essaiRepository;
    private final ProduitRepository produitRepository;
    private final EssaiMapper essaiMapper;

    @Override
    public EssaiResponseDTO create(EssaiRequestDTO requestDTO) {

        Produit produit = produitRepository.findById(requestDTO.getIdProduit())
                .orElseThrow(() -> new RuntimeException("Produit introuvable."));

        if (essaiRepository.existsByNumeroEssai(requestDTO.getNumeroEssai())) {
            throw new RuntimeException("Un essai avec ce numéro existe déjà.");
        }

        Essai essai = essaiMapper.toEntity(requestDTO);
        essai.setProduit(produit);

        Essai saved = essaiRepository.save(essai);

        return essaiMapper.toResponseDTO(saved);
    }

    @Override
    public EssaiResponseDTO update(Long id, EssaiRequestDTO requestDTO) {

        Essai essai = essaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Essai introuvable."));

        Produit produit = produitRepository.findById(requestDTO.getIdProduit())
                .orElseThrow(() -> new RuntimeException("Produit introuvable."));

        if (!essai.getNumeroEssai().equals(requestDTO.getNumeroEssai())
                && essaiRepository.existsByNumeroEssai(requestDTO.getNumeroEssai())) {
            throw new RuntimeException("Un essai avec ce numéro existe déjà.");
        }

        essaiMapper.updateEntityFromDto(requestDTO, essai);
        essai.setProduit(produit);

        Essai updated = essaiRepository.save(essai);

        return essaiMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EssaiResponseDTO getById(Long id) {

        Essai essai = essaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Essai introuvable."));

        return essaiMapper.toResponseDTO(essai);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EssaiResponseDTO> getAll() {

        return essaiRepository.findAll()
                .stream()
                .map(essaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EssaiResponseDTO> getByProduit(Long idProduit) {

        return essaiRepository.findByProduitIdProduit(idProduit)
                .stream()
                .map(essaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EssaiResponseDTO> getByStatut(String statut) {

        return essaiRepository.findByStatut(statut)
                .stream()
                .map(essaiMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Essai essai = essaiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Essai introuvable."));

        essaiRepository.delete(essai);
    }
}