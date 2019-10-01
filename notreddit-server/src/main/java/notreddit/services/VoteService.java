package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.responses.VoteResponseModel;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface VoteService {

    ResponseEntity<?> voteForPost(byte choice, UUID postId, User user);

    List<VoteResponseModel> findVotesByUser(User user);
}
