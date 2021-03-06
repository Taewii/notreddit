package notreddit.repositories;

import notreddit.data.entities.User;
import notreddit.data.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {

    Optional<Vote> findByPostIdAndUserId(UUID postId, UUID userId);

    Optional<Vote> findByCommentIdAndUserId(UUID postId, UUID userId);

    @Query("SELECT v FROM Vote v JOIN FETCH v.post WHERE v.user = :user AND v.comment IS NULL ")
    List<Vote> findPostVotesByUser(@Param("user") User user);

    @Query("SELECT v FROM Vote v JOIN FETCH v.comment WHERE v.user = :user AND v.post IS NULL ")
    List<Vote> findCommentVotesByUser(@Param("user") User user);

    @Query(value = "UPDATE posts SET upvotes = upvotes + 1 " +
            "WHERE id = (SELECT p.id FROM posts p ORDER BY RANDOM() LIMIT 1) " +
            "RETURNING cast(id as varchar)", nativeQuery = true)
    String upvoteRandomPost();

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteAllByCommentId(UUID commentId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteAllByPostId(UUID commentId);
}
