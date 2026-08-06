package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeDocumentRepository extends JpaRepository<TypeDocument, Long> {

    Optional<TypeDocument> findByNomType(String nomType);

    boolean existsByNomType(String nomType);
}