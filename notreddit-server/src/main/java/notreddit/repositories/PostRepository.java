package notreddit.repositories;

import notreddit.domain.entities.Post;
import notreddit.domain.models.responses.PostListResponseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query("SELECT " +
            "new notreddit.domain.models.responses.PostListResponseModel(" +
            "p.id, " +
            "p.creator.username, " +
            "p.title, " +
            "p.file.url, " +
            "p.upvotes, " +
            "p.downvotes, " +
            "p.createdOn, " +
            "size(p.comments)) " +
            "FROM Post p")
    List<PostListResponseModel> allPosts();
}
