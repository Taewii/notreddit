package notreddit.services.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notreddit.constants.ApiResponseMessages;
import notreddit.domain.entities.User;
import notreddit.domain.entities.Votable;
import notreddit.domain.entities.Vote;
import notreddit.domain.models.responses.api.ApiResponse;
import notreddit.domain.models.responses.post.PostVoteUserChoiceResponse;
import notreddit.repositories.CommentRepository;
import notreddit.repositories.PostRepository;
import notreddit.repositories.VoteRepository;
import notreddit.services.VoteService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static notreddit.constants.GeneralConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private static final String COMMENT_VOTES_BY_USER_CACHE = "commentVotesByUser";
    private static final String POST_VOTES_BY_USER_CACHE = "postVotesByUser";
    private static final String USER_CHOICE_BY_POST_CACHE = "userChoiceByPost";

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = POSTS_BY_ID_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_USERNAME_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_SUBREDDIT_CACHE, allEntries = true),
            @CacheEvict(value = SUBSCRIBED_POSTS_CACHE, allEntries = true),
            @CacheEvict(value = COMMENTS_BY_USERNAME, allEntries = true),
            @CacheEvict(value = COMMENTS_BY_POST_CACHE, allEntries = true),
            @CacheEvict(value = COMMENT_VOTES_BY_USER_CACHE, key = "#user.id"),
            @CacheEvict(value = POST_VOTES_BY_USER_CACHE, key = "#user.id"),
            @CacheEvict(value = USER_CHOICE_BY_POST_CACHE,
                    key = "#user.id.toString().concat('-').concat(#postId)",
                    condition = "#postId != null")
    })
    // all the if (postId != null) are cus the method handles votes for both posts and comments
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
                    .ok(new ApiResponse(true, ApiResponseMessages.SUCCESSFUL_VOTE_DELETION));
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

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/comment/vote")
                .buildAndExpand().toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true, ApiResponseMessages.SUCCESSFUL_VOTE_REGISTRATION));
    }

    @Override
    @Cacheable(value = POST_VOTES_BY_USER_CACHE, key = "#user.id")
    public Map<String, Byte> findPostVotesByUser(User user) {
        return voteRepository.findPostVotesByUser(user)
                .parallelStream()
                .collect(Collectors.toMap(
                        v -> v.getPost().getId().toString(),
                        Vote::getChoice));
    }

    @Override
    @Cacheable(value = COMMENT_VOTES_BY_USER_CACHE, key = "#user.id")
    public Map<String, Byte> findCommentVotesByUser(User user) {
        return voteRepository.findCommentVotesByUser(user)
                .parallelStream()
                .collect(Collectors.toMap(
                        v -> v.getComment().getId().toString(),
                        Vote::getChoice));
    }

    @Override
    @Cacheable(value = USER_CHOICE_BY_POST_CACHE, key = "#user.id.toString().concat('-').concat(#postId)")
    public PostVoteUserChoiceResponse getUserChoiceForPost(User user, UUID postId) {
        Vote vote = voteRepository.findByPostIdAndUserId(postId, user.getId()).orElse(null);

        if (vote == null) {
            return new PostVoteUserChoiceResponse(false, null);
        }

        return new PostVoteUserChoiceResponse(true, vote.getChoice());
    }

    @Scheduled(cron = "0 0/5 * * * *") // every 5 minutes
    public void upvoteRandomPost() {
        String postId = voteRepository.upvoteRandomPost();
        log.info("Scheduled task executed: upvoted post: {} at: {}", postId, LocalDateTime.now());
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
