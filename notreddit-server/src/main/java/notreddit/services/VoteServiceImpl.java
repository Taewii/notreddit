package notreddit.services;

import notreddit.domain.entities.Comment;
import notreddit.domain.entities.Post;
import notreddit.domain.entities.User;
import notreddit.domain.entities.Vote;
import notreddit.domain.models.responses.ApiResponse;
import notreddit.domain.models.responses.PostVoteUserChoiceResponse;
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
    @Transactional
    @Override
    public ResponseEntity<?> voteForPostOrComment(byte choice, UUID postId, UUID commentId, User user) {
        Post post = null;
        Comment comment = null;

        if (postId != null) {
            post = postRepository.findById(postId).orElse(null);
        } else {
            comment = commentRepository.findById(commentId).orElse(null);
        }

        if (post == null && comment == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Post/Comment does't exist."));
        }

        Vote vote;
        if (postId != null) {
            vote = voteRepository.findByPostIdAndUserId(postId, user.getId()).orElse(null);
        } else {
            vote = voteRepository.findByCommentIdAndUserId(commentId, user.getId()).orElse(null);
        }

        // if the user clicked the same choice hes already voted for -> deselect the vote
        if (vote != null && vote.getChoice() == choice) {
            if (postId != null) {
                updateVote(true, vote, post, null, (byte) 0);
            } else {
                updateVote(true, vote, null, comment, (byte) 0);
            }

            voteRepository.delete(vote);
            return ResponseEntity
                    .ok()
                    .body(new ApiResponse(true, "Vote deselected successfully."));
        }

        // if user hasn't yet voted for the post/comment, create a new vote, else, update choice
        if (vote == null) {
            vote = new Vote();
            vote.setChoice(choice);
            vote.setPost(post);
            vote.setUser(user);

            if (postId != null) {
                updateVote(false, vote, post, null, choice);
            } else {
                vote.setComment(comment);
                updateVote(false, vote, null, comment, choice);
            }
        } else {
            if (postId != null) {
                updateVote(true, vote, post, null, choice);
            } else {
                updateVote(true, vote, null, comment, choice);
            }
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
        Vote vote = voteRepository.findByPostIdAndUserId(postId, user.getId()).orElse(null);

        if (vote == null) {
            return new PostVoteUserChoiceResponse(false, null);
        }

        return new PostVoteUserChoiceResponse(true, vote.getChoice());
    }

    private void updateVote(boolean alreadyVoted, Vote vote, Post post, Comment comment, byte choice) {
        if (alreadyVoted) {
            if (post != null) {
                if (vote.getChoice() < 0) post.setDownvotes(post.getDownvotes() - 1);
                else post.setUpvotes(post.getUpvotes() - 1);
            } else {
                if (vote.getChoice() < 0) comment.setDownvotes(comment.getDownvotes() - 1);
                else comment.setUpvotes(comment.getUpvotes() - 1);
            }
        }

        if (choice == 1) {
            if (post != null) post.upvote();
            else comment.upvote();
        } else if (choice == -1) {
            if (post != null) post.downvote();
            else comment.downvote();
        }
    }
}
