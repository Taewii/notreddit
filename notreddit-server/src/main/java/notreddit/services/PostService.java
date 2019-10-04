package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.PostCreateRequest;
import notreddit.domain.models.responses.PostDetailsResponseModel;
import notreddit.domain.models.responses.PostListResponseModel;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface PostService {

    List<PostListResponseModel> allPosts();

    ResponseEntity<?> create(PostCreateRequest request, User creator);

    PostDetailsResponseModel findById(UUID id);
}
