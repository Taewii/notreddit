package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.responses.PostVoteUserChoiceResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

public interface VoteService {

    ResponseEntity<?> voteForPost(byte choice, UUID postId, User user);

    Map<String, Byte> findVotesByUser(User user);

    PostVoteUserChoiceResponse getUserChoiceForPost(User user, UUID postId);
}
