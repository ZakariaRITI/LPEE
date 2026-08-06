package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByCodeRegion(String codeRegion);

    boolean existsByCodeRegion(String codeRegion);
}