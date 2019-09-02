package armory.repositories;

import armory.domain.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    @Query("SELECT a FROM Account a JOIN FETCH a.roles WHERE a.id = :id")
    Optional<Account> findByIdWithRoles(@Param("id") UUID id);

    Optional<Account> findByUsernameOrEmail(@NotBlank String username, @Email @NotBlank String email);

    Boolean existsByUsername(@NotBlank String username);

    Boolean existsByEmail(@Email @NotBlank String email);
}
