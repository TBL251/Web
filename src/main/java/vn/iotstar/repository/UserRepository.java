package vn.iotstar.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.iotstar.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Integer id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Integer id);

    @Query("""
            SELECT u FROM User u
            WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(u.fullname, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<User> search(@Param("keyword") String keyword, Pageable pageable);
}
