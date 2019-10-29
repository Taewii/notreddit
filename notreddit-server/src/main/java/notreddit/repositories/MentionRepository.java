package notreddit.repositories;

import notreddit.domain.entities.Mention;
import notreddit.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Repository
public interface MentionRepository extends JpaRepository<Mention, UUID> {

    @Query("SELECT COUNT(m) FROM Mention m WHERE m.isRead = false AND m.receiver = :receiver")
    int getUnreadMentionCountByUser(@NotNull @Param("receiver") User receiver);

    @Query("SELECT m FROM Mention m " +
            "JOIN FETCH m.comment c " +
            "JOIN FETCH c.post " +
            "WHERE m.receiver = :receiver " +
            "ORDER BY m.createdOn DESC")
    List<Mention> getUsersMentions(@NotNull @Param("receiver") User receiver);
}
