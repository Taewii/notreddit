package notreddit.services;

import notreddit.data.entities.User;
import notreddit.data.models.requests.PostCreateRequest;
import notreddit.data.models.requests.PostEditRequest;
import notreddit.data.models.responses.post.PostDetailsResponseModel;
import notreddit.data.models.responses.post.PostEditResponseModel;
import notreddit.data.models.responses.post.PostsResponseModel;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface PostService {

    ResponseEntity<?> create(PostCreateRequest request, User creator);

    ResponseEntity<?> edit(PostEditRequest request, User user);

    ResponseEntity<?> delete(UUID postId, User user);

    PostDetailsResponseModel findById(UUID id);

    PostEditResponseModel getPostEditDetails(UUID id);

    PostsResponseModel allPosts(Pageable pageable);

    PostsResponseModel defaultPosts(Pageable pageable);

    PostsResponseModel subscribedPosts(User user, Pageable pageable);

    PostsResponseModel findAllByUsername(String username, Pageable pageable);

    PostsResponseModel findPostsByVoteChoice(User user, String username, int choice, Pageable pageable);

    PostsResponseModel findAllBySubreddit(String subreddit, Pageable pageable);
}
