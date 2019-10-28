package notreddit.repositories;

import notreddit.domain.entities.Mention;
import notreddit.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Repository
public interface MentionRepository extends JpaRepository<Mention, UUID> {

    @Query("SELECT COUNT(m) FROM Mention m WHERE m.isRead = false AND m.receiver = :receiver")
    int getUnreadMentionCountByUser(@NotNull @Param("receiver") User receiver);
}
