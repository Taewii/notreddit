package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.CommentPostRequestModel;
import notreddit.domain.models.responses.CommentListWithChildrenResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    ResponseEntity<?> create(CommentPostRequestModel commentModel, User creator);

    List<CommentListWithChildrenResponse> findAllFromPost(UUID postId);
}
