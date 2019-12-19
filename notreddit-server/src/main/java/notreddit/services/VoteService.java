package notreddit.services;

import notreddit.data.entities.User;
import notreddit.data.models.responses.post.PostVoteUserChoiceResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

public interface VoteService {

    ResponseEntity<?> voteForPostOrComment(byte choice, UUID postId, UUID commentId, User user);

    PostVoteUserChoiceResponse getUserChoiceForPost(User user, UUID postId);

    Map<String, Byte> findPostVotesByUser(User user);

    Map<String, Byte> findCommentVotesByUser(User user);
}
