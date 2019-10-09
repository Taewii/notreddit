package notreddit.services;

import notreddit.domain.entities.Post;
import notreddit.domain.entities.User;
import notreddit.domain.entities.Vote;
import notreddit.domain.models.responses.ApiResponse;
import notreddit.domain.models.responses.PostVoteUserChoiceResponse;
import notreddit.repositories.PostRepository;
import notreddit.repositories.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Transactional
    @Override
    public ResponseEntity<?> voteForPost(byte choice, UUID postId, User user) {
        Post post = postRepository.findById(postId).orElse(null);

        if (post == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Post does't exist."));
        }

        Vote vote = voteRepository.findByPostIdAndUserId(postId, user.getId()).orElse(null);

        // if the user clicked the same choice hes already voted for -> deselect the vote
        if (vote != null && vote.getChoice() == choice) {
            updatePostVotes(true, vote, post, (byte) 0);
            voteRepository.delete(vote);

            return ResponseEntity
                    .ok()
                    .body(new ApiResponse(true, "Vote deselected successfully."));
        }

        // if user hasn't yet voted for the post, create a new vote, else, update choice
        if (vote == null) {
            vote = new Vote();
            vote.setChoice(choice);
            vote.setPost(post);
            vote.setUser(user);
            updatePostVotes(false, vote, post, choice);
        } else {
            updatePostVotes(true, vote, post, choice);
            vote.setChoice(choice);
        }

        vote.setCreatedOn(LocalDateTime.now());
        voteRepository.saveAndFlush(vote);

        return ResponseEntity
                .ok()
                .body(new ApiResponse(true, "Vote registered successfully."));
    }

    @Override
    public Map<String, Byte> findVotesByUser(User user) {
        return voteRepository.findByUser(user)
                .parallelStream()
                .collect(Collectors.toUnmodifiableMap(
                        v -> v.getPost().getId().toString(),
                        Vote::getChoice));
    }

    @Override
    public PostVoteUserChoiceResponse getUserChoiceForPost(User user, UUID postId) {
        Vote vote = voteRepository.findByUserAndPostId(user, postId).orElse(null);

        if (vote == null) {
            return new PostVoteUserChoiceResponse(false, null);
        }

        return new PostVoteUserChoiceResponse(true, vote.getChoice());
    }

    private void updatePostVotes(boolean alreadyVoted, Vote vote, Post post, byte choice) {
        if (alreadyVoted) {
            if (vote.getChoice() < 0) {
                post.setDownvotes(post.getDownvotes() - 1);
            } else {
                post.setUpvotes(post.getUpvotes() - 1);
            }
        }

        if (choice == 1) {
            post.upvote();
        } else if (choice == -1) {
            post.downvote();
        }
    }
}
