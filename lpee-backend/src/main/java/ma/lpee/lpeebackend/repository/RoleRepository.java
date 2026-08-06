package ma.lpee.lpeebackend.repository;

import ma.lpee.lpeebackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByCodeRole(String codeRole);

    boolean existsByCodeRole(String codeRole);
}