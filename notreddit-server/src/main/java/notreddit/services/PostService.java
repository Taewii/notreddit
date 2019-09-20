package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.PostCreateRequest;
import org.springframework.http.ResponseEntity;

public interface PostService {

    ResponseEntity<?> create(PostCreateRequest request, User creator);
}
