package notreddit.repositories;

import notreddit.domain.entities.Comment;
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
            "LEFT JOIN FETCH c.children " +
            "WHERE c.parent IS NULL " +
            "AND c.post.id = :id")
    List<Comment> findByPostIdWithChildren(@Param("id") UUID id);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.children WHERE c.id = :id")
    Optional<Comment> findByIdWithChildren(@Param("id") UUID id);
}
