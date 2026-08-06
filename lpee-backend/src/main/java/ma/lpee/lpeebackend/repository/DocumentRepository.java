package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByNumeroDocument(String numeroDocument);

    boolean existsByNumeroDocument(String numeroDocument);

    List<Document> findByTypeDocumentIdType(Long idType);
}