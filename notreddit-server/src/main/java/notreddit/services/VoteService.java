package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.responses.PostVoteUserChoiceResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

public interface VoteService {

    ResponseEntity<?> voteForPostOrComment(byte choice, UUID postId, UUID commentId, User user);

    Map<String, Byte> findVotesByUser(User user);

    PostVoteUserChoiceResponse getUserChoiceForPost(User user, UUID postId);
}
