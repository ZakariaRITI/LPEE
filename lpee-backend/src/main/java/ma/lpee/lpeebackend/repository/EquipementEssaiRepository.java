package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.EquipementEssai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipementEssaiRepository extends JpaRepository<EquipementEssai, Long> {

    List<EquipementEssai> findByEssaiIdEssai(Long idEssai);

    List<EquipementEssai> findByEquipementIdEquipement(Long idEquipement);

    boolean existsByEssaiIdEssaiAndEquipementIdEquipement(Long idEssai, Long idEquipement);
}