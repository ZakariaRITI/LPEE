package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.DocumentationEssai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentationEssaiRepository extends JpaRepository<DocumentationEssai, Long> {

    List<DocumentationEssai> findByEssaiIdEssai(Long idEssai);

    List<DocumentationEssai> findByDocumentIdDocument(Long idDocument);

    boolean existsByEssaiIdEssaiAndDocumentIdDocument(Long idEssai, Long idDocument);
}