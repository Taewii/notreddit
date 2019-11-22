package notreddit.services;

import notreddit.constants.ApiResponseMessages;
import notreddit.domain.entities.User;
import notreddit.domain.entities.Votable;
import notreddit.domain.entities.Vote;
import notreddit.domain.models.responses.api.ApiResponse;
import notreddit.domain.models.responses.post.PostVoteUserChoiceResponse;
import notreddit.repositories.CommentRepository;
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
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public VoteServiceImpl(PostRepository postRepository,
                           CommentRepository commentRepository,
                           VoteRepository voteRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.voteRepository = voteRepository;
    }

    // all the if (postId != null) are cus the method handles votes for both posts and comments
    @Override
    @Transactional
    public ResponseEntity<?> voteForPostOrComment(byte choice, UUID postId, UUID commentId, User user) {
        Votable votable;

        if (postId != null) {
            votable = postRepository.findById(postId).orElse(null);
        } else {
            votable = commentRepository.findById(commentId).orElse(null);
        }

        if (votable == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, ApiResponseMessages.NONEXISTENT_POST_OR_COMMENT));
        }

        Vote vote;
        if (postId != null) {
            vote = voteRepository.findByPostIdAndUserId(postId, user.getId()).orElse(null);
        } else {
            vote = voteRepository.findByCommentIdAndUserId(commentId, user.getId()).orElse(null);
        }

        // if the user clicked the same choice hes already voted for -> deselect the vote
        if (vote != null && vote.getChoice() == choice) {
            updateVote(true, vote, votable, (byte) 0);

            voteRepository.delete(vote);
            return ResponseEntity
                    .ok()
                    .body(new ApiResponse(true, ApiResponseMessages.SUCCESSFUL_VOTE_DELETION));
        }

        // if user hasn't yet voted for the post/comment, create a new vote, else, update choice
        if (vote == null) {
            vote = new Vote();
            vote.setChoice(choice);
            vote.setUser(user);

            if (postId != null) {
                vote.setPost(votable);
            } else {
                vote.setComment(votable);
            }
            updateVote(false, vote, votable, choice);
        } else {
            updateVote(true, vote, votable, choice);
            vote.setChoice(choice);
        }

        vote.setCreatedOn(LocalDateTime.now());
        voteRepository.saveAndFlush(vote);

        return ResponseEntity
                .ok()
                .body(new ApiResponse(true, ApiResponseMessages.SUCCESSFUL_VOTE_REGISTRATION));
    }

    @Override
    public Map<String, Byte> findPostVotesByUser(User user) {
        return voteRepository.findPostVotesByUser(user)
                .parallelStream()
                .collect(Collectors.toUnmodifiableMap(
                        v -> v.getPost().getId().toString(),
                        Vote::getChoice));
    }

    @Override
    public Map<String, Byte> findCommentVotesByUser(User user) {
        return voteRepository.findCommentVotesByUser(user)
                .parallelStream()
                .collect(Collectors.toUnmodifiableMap(
                        v -> v.getComment().getId().toString(),
                        Vote::getChoice));
    }

    @Override
    public PostVoteUserChoiceResponse getUserChoiceForPost(User user, UUID postId) {
        Vote vote = voteRepository.findByPostIdAndUserId(postId, user.getId()).orElse(null);

        if (vote == null) {
            return new PostVoteUserChoiceResponse(false, null);
        }

        return new PostVoteUserChoiceResponse(true, vote.getChoice());
    }

    private void updateVote(boolean alreadyVoted, Vote vote, Votable votable, byte choice) {
        if (alreadyVoted) {
            if (vote.getChoice() < 0) {
                votable.setDownvotes(votable.getDownvotes() - 1);
            } else {
                votable.setUpvotes(votable.getUpvotes() - 1);
            }
        }

        if (choice == 1) {
            votable.upvote();
        } else if (choice == -1) {
            votable.downvote();
        }
    }
}
