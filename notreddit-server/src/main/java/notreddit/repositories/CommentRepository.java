package notreddit.repositories;

import notreddit.domain.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("SELECT c FROM Comment c " +
            "WHERE c.parent IS NULL " +
            "AND c.post.id = :id")
    List<Comment> findByPostIdWithChildren(@Param("id") UUID id);

    @Query("SELECT c FROM Comment c WHERE c.id = :id")
    Optional<Comment> findByIdWithChildren(@Param("id") UUID id);

    @Query(value = "SELECT c FROM Comment c " +
            "JOIN FETCH c.post " +
            "WHERE LOWER(c.creator.username) = :username",
            countQuery = "SELECT COUNT(c) " +
                    "FROM Comment c " +
                    "WHERE LOWER(c.creator.username) = :username")
    Page<Comment> findByCreatorUsername(@Param("username") String username, Pageable pageable);
}
