package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.PostCreateRequest;
import notreddit.domain.models.responses.post.PostDetailsResponseModel;
import notreddit.domain.models.responses.post.PostsResponseModel;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface PostService {

    PostsResponseModel allPosts(Pageable pageable);

    ResponseEntity<?> create(PostCreateRequest request, User creator);

    PostDetailsResponseModel findById(UUID id);

    PostsResponseModel findAllByUsername(String username, Pageable pageable);

    PostsResponseModel getPostsByVoteChoice(User user, String username, int choice, Pageable pageable);

    PostsResponseModel findAllBySubreddit(String subreddit, Pageable pageable);

    ResponseEntity<?> delete(UUID postId, User user);
}
