package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.CommentPostRequestModel;
import org.springframework.http.ResponseEntity;

public interface CommentService {

    ResponseEntity<?> create(CommentPostRequestModel commentModel, User creator);
}
