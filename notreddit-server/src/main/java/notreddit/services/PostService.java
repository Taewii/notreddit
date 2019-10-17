package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.PostCreateRequest;
import notreddit.domain.models.responses.PostDetailsResponseModel;
import notreddit.domain.models.responses.PostsResponseModel;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface PostService {

    PostsResponseModel allPosts(Pageable pageable);

    ResponseEntity<?> create(PostCreateRequest request, User creator);

    PostDetailsResponseModel findById(UUID id);
}
