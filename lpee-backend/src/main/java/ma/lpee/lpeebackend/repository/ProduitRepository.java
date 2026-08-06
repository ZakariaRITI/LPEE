package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    Optional<Produit> findByCodeProduit(String codeProduit);

    boolean existsByCodeProduit(String codeProduit);

    List<Produit> findByFamilleProduitIdFamille(Long idFamille);
}