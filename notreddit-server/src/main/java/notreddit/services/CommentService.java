package notreddit.services;

import notreddit.data.entities.User;
import notreddit.data.models.requests.CommentCreateRequest;
import notreddit.data.models.requests.CommentEditRequest;
import notreddit.data.models.responses.comment.CommentListWithChildren;
import notreddit.data.models.responses.comment.CommentsResponseModel;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface CommentService {

    ResponseEntity<?> create(CommentCreateRequest commentModel, User creator);

    ResponseEntity<?> edit(CommentEditRequest commentModel, User user);

    ResponseEntity<?> delete(UUID commentId, User user);

    List<CommentListWithChildren> findAllFromPost(UUID postId, Pageable pageable);

    CommentsResponseModel findAllFromUsername(String username, Pageable pageable);

}
