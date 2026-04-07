package edu.hotel.auth.repository;

import edu.hotel.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(value = """
        SELECT * FROM users
        WHERE (:role IS NULL OR role = :role)
        AND (:active IS NULL OR active = :active)
        """,
            countQuery = """
        SELECT COUNT(*) FROM users
        WHERE (:role IS NULL OR role = :role)
        AND (:active IS NULL OR active = :active)
        """,
            nativeQuery = true)
    Page<User> findAllWithFilters(
            @Param("role") String role,
            @Param("active") Boolean active,
            Pageable pageable
    );
}
