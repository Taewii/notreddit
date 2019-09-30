package notreddit.services;

import notreddit.domain.entities.User;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface VoteService {

    ResponseEntity<?> voteForPost(byte choice, UUID postId, User user);
}
