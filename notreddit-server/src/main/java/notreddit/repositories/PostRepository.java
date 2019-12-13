package notreddit.repositories;

import notreddit.domain.entities.Post;
import notreddit.domain.entities.Subreddit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.creator " +
            "LEFT JOIN FETCH p.subreddit " +
            "LEFT JOIN FETCH p.comments ",
            countQuery = "SELECT COUNT(p) FROM Post p")
    Page<Post> findAll(Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.creator c " +
            "LEFT JOIN FETCH p.subreddit " +
            "LEFT JOIN FETCH p.comments " +
            "WHERE LOWER(c.username) = :username",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE LOWER(p.creator.username) = :username")
    Page<Post> findAllByUsername(@Param("username") String username, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.creator c " +
            "LEFT JOIN FETCH p.subreddit " +
            "LEFT JOIN FETCH p.comments " +
            "WHERE p.id IN :subscriptions")
    List<Post> getPostsFromIdList(@Param("subscriptions") List<UUID> subscriptions, Sort sort);

    @Query(value = "SELECT p.id FROM Post p " +
            "WHERE LOWER(p.subreddit.title) = :title",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.subreddit.title = :title")
    Page<UUID> getPostIdsBySubredditTitle(@Param("title") String title, Pageable pageable);

    @Query(value = "SELECT p.id FROM Post p " +
            "WHERE p.subreddit IN :subscriptions",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.subreddit IN :subscriptions")
    Page<UUID> getSubscribedPostsIds(@Param("subscriptions") Set<Subreddit> subscriptions, Pageable pageable);

    @Query(value = "SELECT cast(p.id as varchar) FROM posts p " +
            "LEFT JOIN votes v ON v.post_id = p.id " +
            "WHERE v.comment_id IS NULL " +
            "AND v.choice = :choice " +
            "AND v.user_id = :userId ",
            countQuery = "SELECT COUNT(p.id) FROM posts p " +
                    "LEFT JOIN votes v ON v.post_id = p.id " +
                    "WHERE v.comment_id IS NULL " +
                    "AND v.choice = :choice " +
                    "AND v.user_id = :userId ",
            nativeQuery = true)
    Page<String> findPostIdsByUserAndVoteChoice(@Param("userId") UUID userId,
                                                @Param("choice") byte choice,
                                                Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.creator " +
            "LEFT JOIN FETCH p.subreddit " +
            "LEFT JOIN FETCH p.comments " +
            "WHERE p.id = :id")
    Optional<Post> findByIdEager(@Param("id") UUID id);

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.creator " +
            "LEFT JOIN FETCH p.comments " +
            "WHERE p.id = :id")
    Optional<Post> findByIdWithCreatorAndComments(@Param("id") UUID id);

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.creator " +
            "LEFT JOIN FETCH p.file " +
            "LEFT JOIN FETCH p.subreddit " +
            "WHERE p.id = :id")
    Optional<Post> findByIdWithFileAnSubreddit(@Param("id") UUID id);
}
