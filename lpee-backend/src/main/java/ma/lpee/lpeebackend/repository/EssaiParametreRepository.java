package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.EssaiParametre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EssaiParametreRepository extends JpaRepository<EssaiParametre, Long> {

    List<EssaiParametre> findByEssaiIdEssai(Long idEssai);

    List<EssaiParametre> findByParametreIdParametre(Long idParametre);

    boolean existsByEssaiIdEssaiAndParametreIdParametre(Long idEssai, Long idParametre);
}