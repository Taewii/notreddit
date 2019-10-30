package notreddit.repositories;

import notreddit.domain.entities.Mention;
import notreddit.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MentionRepository extends JpaRepository<Mention, UUID> {

    @Query("SELECT m FROM Mention m JOIN FETCH m.receiver WHERE m.id = :id")
    Optional<Mention> findByIdWithReceiver(@NotNull @Param("id") UUID id);

    @Query("SELECT COUNT(m) FROM Mention m WHERE m.isRead = false AND m.receiver = :receiver")
    int getUnreadMentionCountByUser(@NotNull @Param("receiver") User receiver);

    @Query(value = "SELECT m FROM Mention m " +
            "JOIN FETCH m.comment c " +
            "JOIN FETCH c.post " +
            "WHERE m.receiver = :receiver " +
            "ORDER BY m.isRead, m.createdOn DESC",
            countQuery = "SELECT COUNT(m) FROM Mention m WHERE m.receiver = :receiver")
    Page<Mention> getUsersMentions(@NotNull @Param("receiver") User receiver, Pageable pageable);
}
