package notreddit.repositories;

import notreddit.domain.entities.Subreddit;
import notreddit.domain.models.responses.subreddit.SubredditWithPostCountResponse;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import java.util.*;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Long> {

    Boolean existsByTitleIgnoreCase(@Length(min = 4) @NotBlank String title);

    Optional<Subreddit> findByTitleIgnoreCase(@Length(min = 4) @NotBlank String title);

    @Query("SELECT new notreddit.domain.models.responses.subreddit.SubredditWithPostCountResponse(s.title, s.posts.size, s.subscribers.size) " +
            "FROM Subreddit s ORDER BY s.title")
    List<SubredditWithPostCountResponse> findAllWithPostCount();

    @Query("SELECT s FROM Subreddit s WHERE LOWER(s.title) IN :title")
    Set<Subreddit> findByTitleIn(Collection<@NotBlank @Length(min = 3) String> title);

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
            "FROM user_subscriptions us " +
            "JOIN subreddits s ON s.id = us.subreddit_id " +
            "WHERE s.title = :subredditTitle AND us.user_id = :userId",
            nativeQuery = true)
    Boolean isUserSubscribedToSubreddit(@Param("subredditTitle") String subreddit, @Param("userId") UUID userId);
}
