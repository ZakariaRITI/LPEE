package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.HistoriqueAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Long> {
    List<HistoriqueAction> findAllByOrderByDateHeureDesc();
}
