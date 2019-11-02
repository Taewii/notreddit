package notreddit.repositories;

import notreddit.domain.entities.Post;
import notreddit.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
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
            "WHERE LOWER(p.subreddit.title) = :subreddit",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE LOWER(p.subreddit.title) = :subreddit")
    Page<Post> findAllBySubreddit(@Param("subreddit") String subreddit, Pageable pageable);

    @Query(value = "SELECT p FROM Vote v " +
            "JOIN v.post as p " +
            "LEFT JOIN FETCH p.subreddit " +
            "LEFT JOIN FETCH p.comments " +
            "LEFT JOIN FETCH p.creator " +
            "WHERE v.comment IS NULL " +
            "AND v.choice = :choice " +
            "AND v.user = :user",
            countQuery = "SELECT COUNT(v) FROM Vote v " +
                    "WHERE v.comment IS NULL " +
                    "AND v.choice = 1 " +
                    "AND v.user = :user")
    Page<Post> findPostsByUserAndVoteChoice(@Param("user") User user,
                                            @Param("choice") byte choice,
                                            Pageable pageable);

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.creator " +
            "LEFT JOIN FETCH p.subreddit " +
            "LEFT JOIN FETCH p.comments " +
            "WHERE p.id = :id")
    Optional<Post> findByIdEager(@Param("id") UUID id);
}
