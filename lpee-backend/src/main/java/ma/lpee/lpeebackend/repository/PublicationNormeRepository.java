package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.PublicationNorme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationNormeRepository extends JpaRepository<PublicationNorme, Long> {

    List<PublicationNorme> findByNormeIdNorme(Long idNorme);

    List<PublicationNorme> findByOrganismeIdOrganisme(Long idOrganisme);

    boolean existsByNormeIdNormeAndOrganismeIdOrganisme(Long idNorme, Long idOrganisme);
}