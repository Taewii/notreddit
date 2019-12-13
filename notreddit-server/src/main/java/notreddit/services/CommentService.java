package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.CommentCreateRequest;
import notreddit.domain.models.requests.CommentEditRequest;
import notreddit.domain.models.responses.comment.CommentListWithChildren;
import notreddit.domain.models.responses.comment.CommentsResponseModel;
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
