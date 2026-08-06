package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.Unite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UniteRepository extends JpaRepository<Unite, Long> {

    Optional<Unite> findByCodeUnite(String codeUnite);

    boolean existsByCodeUnite(String codeUnite);

    List<Unite> findByRegionIdRegion(Long idRegion);
}