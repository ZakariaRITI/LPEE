package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.EssaiParametre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EssaiParametreRepository extends JpaRepository<EssaiParametre, Long> {

    List<EssaiParametre> findByEssaiIdEssai(Long idEssai);

    List<EssaiParametre> findByParametreIdParametre(Long idParametre);

    Optional<EssaiParametre> findByEssaiIdEssaiAndParametreIdParametre(Long idEssai, Long idParametre);
}
