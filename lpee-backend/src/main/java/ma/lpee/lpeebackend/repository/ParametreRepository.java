package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.Parametre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParametreRepository extends JpaRepository<Parametre, Long> {

    Optional<Parametre> findByNomParametre(String nomParametre);

    boolean existsByNomParametre(String nomParametre);
}