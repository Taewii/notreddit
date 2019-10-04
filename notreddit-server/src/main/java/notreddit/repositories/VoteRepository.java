package notreddit.repositories;

import notreddit.domain.entities.User;
import notreddit.domain.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {

    Optional<Vote> findByPostIdAndUserId(UUID postId, UUID userId);

    @Query("SELECT v FROM Vote v JOIN FETCH v.post WHERE v.user = :user")
    List<Vote> findByUser(@Param("user") User user);

    Optional<Vote> findByUserAndPostId(@NotNull User user, UUID postId);
}
