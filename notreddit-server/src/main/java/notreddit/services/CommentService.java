package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.CommentCreateRequestModel;
import notreddit.domain.models.requests.CommentEditRequestModel;
import notreddit.domain.models.responses.comment.CommentListWithChildren;
import notreddit.domain.models.responses.comment.CommentsResponseModel;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    ResponseEntity<?> create(CommentCreateRequestModel commentModel, User creator);

    List<CommentListWithChildren> findAllFromPost(UUID postId);

    CommentsResponseModel findAllFromUsername(String username, Pageable pageable);

    ResponseEntity<?> delete(UUID commentId, User user);

    ResponseEntity<?> edit(CommentEditRequestModel commentModel, User user);
}
