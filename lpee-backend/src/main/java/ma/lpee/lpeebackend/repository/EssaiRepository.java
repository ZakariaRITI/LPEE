package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.Essai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EssaiRepository extends JpaRepository<Essai, Long> {

    Optional<Essai> findByNumeroEssai(String numeroEssai);

    boolean existsByNumeroEssai(String numeroEssai);

    List<Essai> findByProduitIdProduit(Long idProduit);

    List<Essai> findByStatut(String statut);
}