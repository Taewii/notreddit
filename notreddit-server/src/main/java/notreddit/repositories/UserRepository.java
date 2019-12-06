package notreddit.repositories;

import notreddit.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") UUID id);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = ?1 OR u.email = ?2")
    Optional<User> findByUsernameOrEmailIgnoreCase(@NotBlank String username, @Email @NotBlank String email);

    Boolean existsByUsernameIgnoreCase(@NotBlank String username);

    Boolean existsByEmailIgnoreCase(@Email @NotBlank String email);

    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.roles r")
    List<User> findAllWithRoles();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.subscriptions WHERE u = :user")
    User getWithSubscriptions(@Param("user") User user);
}
