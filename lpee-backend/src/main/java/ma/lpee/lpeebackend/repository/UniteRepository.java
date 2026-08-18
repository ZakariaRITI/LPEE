package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.Unite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UniteRepository extends JpaRepository<Unite, Long> {

    Optional<Unite> findByCodeUnite(String codeUnite);

    boolean existsByCodeUnite(String codeUnite);

    Page<Unite> findByCodeUniteContainingIgnoreCase(String codeUnite, Pageable pageable);

    List<Unite> findByRegionIdRegion(Long idRegion);
}
