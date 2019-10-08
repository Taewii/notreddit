package notreddit.repositories;

import notreddit.domain.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN FETCH p.creator " +
            "LEFT JOIN FETCH p.subreddit " +
            "LEFT JOIN FETCH p.comments ")
    List<Post> findAll();

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.creator " +
            "LEFT JOIN FETCH p.subreddit " +
            "LEFT JOIN FETCH p.comments " +
            "WHERE p.id = :id")
    Optional<Post> findById(@Param("id") UUID id);
}
