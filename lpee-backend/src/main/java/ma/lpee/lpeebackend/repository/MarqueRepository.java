package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.Marque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarqueRepository extends JpaRepository<Marque, Long> {

    Optional<Marque> findByNomMarque(String nomMarque);

    boolean existsByNomMarque(String nomMarque);
}