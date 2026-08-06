package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.ConformiteNorme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConformiteNormeRepository extends JpaRepository<ConformiteNorme, Long> {

    List<ConformiteNorme> findByEssaiIdEssai(Long idEssai);

    List<ConformiteNorme> findByNormeIdNorme(Long idNorme);

    boolean existsByEssaiIdEssaiAndNormeIdNorme(Long idEssai, Long idNorme);
}