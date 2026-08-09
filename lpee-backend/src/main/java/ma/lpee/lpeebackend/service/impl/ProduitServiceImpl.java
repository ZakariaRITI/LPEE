package ma.lpee.lpeebackend.service.impl;

import lombok.RequiredArgsConstructor;
import ma.lpee.lpeebackend.dto.request.ProduitRequestDTO;
import ma.lpee.lpeebackend.dto.response.ProduitResponseDTO;
import ma.lpee.lpeebackend.entity.FamilleProduit;
import ma.lpee.lpeebackend.entity.Produit;
import ma.lpee.lpeebackend.exception.DuplicateResourceException;
import ma.lpee.lpeebackend.exception.ResourceNotFoundException;
import ma.lpee.lpeebackend.mapper.ProduitMapper;
import ma.lpee.lpeebackend.repository.FamilleProduitRepository;
import ma.lpee.lpeebackend.repository.ProduitRepository;
import ma.lpee.lpeebackend.service.ProduitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProduitServiceImpl implements ProduitService {

    private final ProduitRepository produitRepository;
    private final FamilleProduitRepository familleProduitRepository;
    private final ProduitMapper produitMapper;

    @Override
    public ProduitResponseDTO create(ProduitRequestDTO requestDTO) {

        FamilleProduit familleProduit = familleProduitRepository.findById(requestDTO.getIdFamille())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Famille produit introuvable."
                ));

        if (produitRepository.existsByCodeProduit(requestDTO.getCodeProduit())) {
            throw new DuplicateResourceException(
                    "Un produit avec ce code existe déjà."
            );
        }

        Produit produit = produitMapper.toEntity(requestDTO);
        produit.setFamilleProduit(familleProduit);

        Produit saved = produitRepository.save(produit);

        return produitMapper.toResponseDTO(saved);
    }

    @Override
    public ProduitResponseDTO update(Long id, ProduitRequestDTO requestDTO) {

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit introuvable."
                ));

        FamilleProduit familleProduit = familleProduitRepository.findById(requestDTO.getIdFamille())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Famille produit introuvable."
                ));

        if (!produit.getCodeProduit().equals(requestDTO.getCodeProduit())
                && produitRepository.existsByCodeProduit(requestDTO.getCodeProduit())) {
            throw new DuplicateResourceException(
                    "Un produit avec ce code existe déjà."
            );
        }

        produitMapper.updateEntityFromDto(requestDTO, produit);
        produit.setFamilleProduit(familleProduit);

        Produit updated = produitRepository.save(produit);

        return produitMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ProduitResponseDTO getById(Long id) {

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit introuvable."
                ));

        return produitMapper.toResponseDTO(produit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduitResponseDTO> getAll() {

        return produitRepository.findAll()
                .stream()
                .map(produitMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProduitResponseDTO> getByFamille(Long idFamille) {

        return produitRepository.findByFamilleProduitIdFamille(idFamille)
                .stream()
                .map(produitMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Long id) {

        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit introuvable."
                ));

        produitRepository.delete(produit);
    }
}