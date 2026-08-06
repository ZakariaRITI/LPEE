package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.Organisme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganismeRepository extends JpaRepository<Organisme, Long> {

    Optional<Organisme> findByCodeOrganisme(String codeOrganisme);

    boolean existsByCodeOrganisme(String codeOrganisme);
}