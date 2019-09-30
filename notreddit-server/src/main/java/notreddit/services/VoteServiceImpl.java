package notreddit.services;

import notreddit.domain.entities.Post;
import notreddit.domain.entities.User;
import notreddit.domain.entities.Vote;
import notreddit.domain.models.responses.ApiResponse;
import notreddit.repositories.PostRepository;
import notreddit.repositories.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VoteServiceImpl implements VoteService {

    private final PostRepository postRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public VoteServiceImpl(PostRepository postRepository,
                           VoteRepository voteRepository) {
        this.postRepository = postRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    public ResponseEntity<?> voteForPost(byte choice, UUID postId, User user) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Post does't exist."));
        }

        Vote vote = voteRepository.findByPostIdAndUserId(postId, user.getId()).orElse(null);

        if (vote == null) {
            vote = new Vote();
            vote.setChoice(choice);
            vote.setPost(post);
            vote.setUser(user);
        } else {
            vote.setChoice(choice);
        }

        vote.setCreatedAt(LocalDateTime.now());
        voteRepository.saveAndFlush(vote);

        return ResponseEntity
                .ok()
                .body(new ApiResponse(true, "Vote registered successfully."));
    }
}
