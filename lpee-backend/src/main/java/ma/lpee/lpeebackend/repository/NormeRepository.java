package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.Norme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NormeRepository extends JpaRepository<Norme, Long> {

    Optional<Norme> findByCodeNorme(String codeNorme);

    Optional<Norme> findByNumeroNorme(String numeroNorme);

    boolean existsByCodeNorme(String codeNorme);
}