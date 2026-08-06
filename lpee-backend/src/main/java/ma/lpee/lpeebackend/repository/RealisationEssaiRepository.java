package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.RealisationEssai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RealisationEssaiRepository extends JpaRepository<RealisationEssai, Long> {

    List<RealisationEssai> findByEssaiIdEssai(Long idEssai);

    List<RealisationEssai> findByUniteIdUnite(Long idUnite);

    List<RealisationEssai> findByCreePar(Long idUser);

    List<RealisationEssai> findByStatut(String statut);
}