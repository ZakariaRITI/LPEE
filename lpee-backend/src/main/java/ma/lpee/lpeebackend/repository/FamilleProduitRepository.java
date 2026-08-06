package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.FamilleProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilleProduitRepository extends JpaRepository<FamilleProduit, Long> {

    Optional<FamilleProduit> findByCodeFamille(String codeFamille);

    boolean existsByCodeFamille(String codeFamille);
}