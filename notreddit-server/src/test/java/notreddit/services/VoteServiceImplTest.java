package notreddit.services;

import notreddit.domain.entities.*;
import notreddit.domain.models.responses.post.PostVoteUserChoiceResponse;
import notreddit.repositories.CommentRepository;
import notreddit.repositories.PostRepository;
import notreddit.repositories.VoteRepository;
import notreddit.services.implementations.VoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VoteServiceImplTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private VoteServiceImpl voteService;

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        MockitoAnnotations.initMocks(this);
    }

    private List<Vote> createVotes(int count) {
        List<Vote> votes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Post post = new Post();
            post.setId(UUID.randomUUID());
            Comment comment = new Comment();
            comment.setId(UUID.randomUUID());

            Vote vote = new Vote();
            vote.setChoice(i % 2 == 0 ? (byte) 1 : (byte) -1);
            vote.setPost(post);
            vote.setComment(comment);
            votes.add(vote);
        }

        return votes;
    }

    @Test
    void voteForPostOrComment_withPostAndSameVoteUpvotedChoice_shouldUpdatePostVotesAndDeleteVote() {
        byte choice = 1;
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Post post = mock(Post.class);
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));

        Vote vote = mock(Vote.class);
        when(vote.getChoice()).thenReturn(choice);
        when(voteRepository.findByPostIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(vote));

        voteService.voteForPostOrComment(choice, UUID.randomUUID(), null, user);

        verify(voteRepository).delete(vote);
        verify(post).setUpvotes(any(int.class));
        verify(post).getUpvotes();
        verify(post, never()).setDownvotes(any(int.class));
        verify(post, never()).getDownvotes();
        verify(post, never()).upvote();
        verify(post, never()).downvote();
        verify(voteRepository, never()).findByCommentIdAndUserId(any(UUID.class), any(UUID.class));
        verify(voteRepository, never()).saveAndFlush(any(Vote.class));
        verify(commentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void voteForPostOrComment_withPostAndSameVoteDownvotedChoice_shouldUpdatePostVotesAndDeleteVote() {
        byte choice = -1;
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Post post = mock(Post.class);
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));

        Vote vote = mock(Vote.class);
        when(vote.getChoice()).thenReturn(choice);
        when(voteRepository.findByPostIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(vote));

        voteService.voteForPostOrComment(choice, UUID.randomUUID(), null, user);

        verify(voteRepository).delete(vote);
        verify(post).setDownvotes(any(int.class));
        verify(post).getDownvotes();
        verify(post, never()).setUpvotes(any(int.class));
        verify(post, never()).getUpvotes();
        verify(post, never()).upvote();
        verify(post, never()).downvote();
        verify(voteRepository, never()).findByCommentIdAndUserId(any(UUID.class), any(UUID.class));
        verify(voteRepository, never()).saveAndFlush(any(Vote.class));
        verify(commentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void voteForPostOrComment_withPostAndDifferentDownvotedChoice_shouldUpdatePostDownvotesUpvotePostAndSave() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Post post = mock(Post.class);
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));

        Vote vote = mock(Vote.class);
        when(vote.getChoice()).thenReturn((byte) -1);
        when(voteRepository.findByPostIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(vote));

        voteService.voteForPostOrComment((byte) 1, UUID.randomUUID(), null, user);

        verify(vote).setChoice((byte) 1);
        verify(post).setDownvotes(any(int.class));
        verify(post).getDownvotes();
        verify(post).upvote();
        verify(voteRepository).saveAndFlush(any(Vote.class));
        verify(post, never()).downvote();
        verify(post, never()).setUpvotes(any(int.class));
        verify(post, never()).getUpvotes();
        verify(vote, never()).setPost(any(Votable.class));
        verify(vote, never()).setComment(any(Votable.class));
        verify(vote, never()).setUser(any(User.class));
        verify(voteRepository, never()).delete(vote);
        verify(voteRepository, never()).findByCommentIdAndUserId(any(UUID.class), any(UUID.class));
        verify(commentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void voteForPostOrComment_withPostAndDifferentUpvotedChoice_shouldUpdatePostUpvotesDownvotePostAndSave() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Post post = mock(Post.class);
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));

        Vote vote = mock(Vote.class);
        when(vote.getChoice()).thenReturn((byte) 1);
        when(voteRepository.findByPostIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(vote));

        voteService.voteForPostOrComment((byte) -1, UUID.randomUUID(), null, user);

        verify(vote).setChoice((byte) -1);
        verify(post).setUpvotes(any(int.class));
        verify(post).getUpvotes();
        verify(post).downvote();
        verify(voteRepository).saveAndFlush(any(Vote.class));
        verify(post, never()).setDownvotes(any(int.class));
        verify(post, never()).getDownvotes();
        verify(post, never()).upvote();
        verify(vote, never()).setPost(any(Votable.class));
        verify(vote, never()).setComment(any(Votable.class));
        verify(vote, never()).setUser(any(User.class));
        verify(voteRepository, never()).delete(vote);
        verify(voteRepository, never()).findByCommentIdAndUserId(any(UUID.class), any(UUID.class));
        verify(commentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void voteForPostOrComment_withPostNoVoteAndUpvotedChoice_shouldUpvotePostCreateVoteAndSaveIt() {
        byte choice = 1;
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Post post = mock(Post.class);
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));
        when(voteRepository.findByPostIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.empty());

        voteService.voteForPostOrComment(choice, UUID.randomUUID(), null, user);

        verify(post).upvote();
        verify(voteRepository).saveAndFlush(any(Vote.class));
        verify(post, never()).downvote();
        verify(post, never()).setUpvotes(any(int.class));
        verify(post, never()).getUpvotes();
        verify(post, never()).setDownvotes(any(int.class));
        verify(post, never()).getDownvotes();
        verify(voteRepository, never()).delete(any(Vote.class));
        verify(voteRepository, never()).findByCommentIdAndUserId(any(UUID.class), any(UUID.class));
        verify(commentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void voteForPostOrComment_withPostNoVoteAndDownvotedChoice_shouldDownvotePostCreateVoteAndSaveIt() {
        byte choice = -1;
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Post post = mock(Post.class);
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));
        when(voteRepository.findByPostIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.empty());

        voteService.voteForPostOrComment(choice, UUID.randomUUID(), null, user);

        verify(post).downvote();
        verify(voteRepository).saveAndFlush(any(Vote.class));
        verify(post, never()).upvote();
        verify(post, never()).setUpvotes(any(int.class));
        verify(post, never()).getUpvotes();
        verify(post, never()).setDownvotes(any(int.class));
        verify(post, never()).getDownvotes();
        verify(voteRepository, never()).delete(any(Vote.class));
        verify(voteRepository, never()).findByCommentIdAndUserId(any(UUID.class), any(UUID.class));
        verify(commentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void voteForPostOrComment_withCommentAndSameVoteUpvotedChoice_shouldUpdatePostVotesAndDeleteVote() {
        byte choice = 1;
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Comment comment = mock(Comment.class);
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        Vote vote = mock(Vote.class);
        when(vote.getChoice()).thenReturn(choice);
        when(voteRepository.findByCommentIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(vote));

        voteService.voteForPostOrComment(choice, null, UUID.randomUUID(), user);

        verify(voteRepository).delete(vote);
        verify(comment).setUpvotes(any(int.class));
        verify(comment).getUpvotes();
        verify(comment, never()).setDownvotes(any(int.class));
        verify(comment, never()).getDownvotes();
        verify(comment, never()).upvote();
        verify(comment, never()).downvote();
        verify(voteRepository, never()).findByPostIdAndUserId(any(UUID.class), any(UUID.class));
        verify(postRepository, never()).findById(any(UUID.class));
        verify(voteRepository, never()).saveAndFlush(any(Vote.class));
    }

    @Test
    void voteForPostOrComment_withCommentAndSameVoteDownvotedChoice_shouldUpdatePostVotesAndDeleteVote() {
        byte choice = -1;
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Comment comment = mock(Comment.class);
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        Vote vote = mock(Vote.class);
        when(vote.getChoice()).thenReturn(choice);
        when(voteRepository.findByCommentIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(vote));

        voteService.voteForPostOrComment(choice, null, UUID.randomUUID(), user);

        verify(voteRepository).delete(vote);
        verify(comment).setDownvotes(any(int.class));
        verify(comment).getDownvotes();
        verify(comment, never()).setUpvotes(any(int.class));
        verify(comment, never()).getUpvotes();
        verify(comment, never()).upvote();
        verify(comment, never()).downvote();
        verify(voteRepository, never()).findByPostIdAndUserId(any(UUID.class), any(UUID.class));
        verify(postRepository, never()).findById(any(UUID.class));
        verify(voteRepository, never()).saveAndFlush(any(Vote.class));
    }

    @Test
    void voteForPostOrComment_withCommentAndDifferentDownvotedChoice_shouldUpdatePostDownvotesUpvotePostAndSave() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Comment comment = mock(Comment.class);
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        Vote vote = mock(Vote.class);
        when(vote.getChoice()).thenReturn((byte) -1);
        when(voteRepository.findByCommentIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(vote));

        voteService.voteForPostOrComment((byte) 1, null, UUID.randomUUID(), user);

        verify(vote).setChoice((byte) 1);
        verify(comment).setDownvotes(any(int.class));
        verify(comment).getDownvotes();
        verify(comment).upvote();
        verify(voteRepository).saveAndFlush(any(Vote.class));
        verify(comment, never()).downvote();
        verify(comment, never()).setUpvotes(any(int.class));
        verify(comment, never()).getUpvotes();
        verify(vote, never()).setPost(any(Votable.class));
        verify(vote, never()).setComment(any(Votable.class));
        verify(vote, never()).setUser(any(User.class));
        verify(voteRepository, never()).delete(vote);
        verify(postRepository, never()).findById(any(UUID.class));
        verify(voteRepository, never()).findByPostIdAndUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void voteForPostOrComment_withCommentAndDifferentUpvotedChoice_shouldUpdatePostUpvotesDownvotePostAndSave() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Comment comment = mock(Comment.class);
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        Vote vote = mock(Vote.class);
        when(vote.getChoice()).thenReturn((byte) 1);
        when(voteRepository.findByCommentIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.of(vote));

        voteService.voteForPostOrComment((byte) -1, null, UUID.randomUUID(), user);

        verify(vote).setChoice((byte) -1);
        verify(comment).setUpvotes(any(int.class));
        verify(comment).getUpvotes();
        verify(comment).downvote();
        verify(voteRepository).saveAndFlush(any(Vote.class));
        verify(comment, never()).setDownvotes(any(int.class));
        verify(comment, never()).getDownvotes();
        verify(comment, never()).upvote();
        verify(vote, never()).setPost(any(Votable.class));
        verify(vote, never()).setComment(any(Votable.class));
        verify(vote, never()).setUser(any(User.class));
        verify(voteRepository, never()).delete(vote);
        verify(postRepository, never()).findById(any(UUID.class));
        verify(voteRepository, never()).findByPostIdAndUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void voteForPostOrComment_withCommentNoVoteAndUpvotedChoice_shouldUpvotePostCreateVoteAndSaveIt() {
        byte choice = 1;
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Comment comment = mock(Comment.class);
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));
        when(voteRepository.findByCommentIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.empty());

        voteService.voteForPostOrComment(choice, null, UUID.randomUUID(), user);

        verify(comment).upvote();
        verify(voteRepository).saveAndFlush(any(Vote.class));
        verify(comment, never()).downvote();
        verify(comment, never()).setUpvotes(any(int.class));
        verify(comment, never()).getUpvotes();
        verify(comment, never()).setDownvotes(any(int.class));
        verify(comment, never()).getDownvotes();
        verify(voteRepository, never()).delete(any(Vote.class));
        verify(postRepository, never()).findById(any(UUID.class));
        verify(voteRepository, never()).findByPostIdAndUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void voteForPostOrComment_withCommentNoVoteAndDownvotedChoice_shouldDownvotePostCreateVoteAndSaveIt() {
        byte choice = -1;
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        Comment comment = mock(Comment.class);
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));
        when(voteRepository.findByCommentIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.empty());

        voteService.voteForPostOrComment(choice, null, UUID.randomUUID(), user);

        verify(comment).downvote();
        verify(voteRepository).saveAndFlush(any(Vote.class));
        verify(comment, never()).upvote();
        verify(comment, never()).setUpvotes(any(int.class));
        verify(comment, never()).getUpvotes();
        verify(comment, never()).setDownvotes(any(int.class));
        verify(comment, never()).getDownvotes();
        verify(voteRepository, never()).delete(any(Vote.class));
        verify(postRepository, never()).findById(any(UUID.class));
        verify(voteRepository, never()).findByPostIdAndUserId(any(UUID.class), any(UUID.class));
    }

    @Test
    void voteForPostOrComment_withNonExistingComment_shouldDoNothing() {
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        voteService.voteForPostOrComment((byte) 1, null, UUID.randomUUID(), new User());

        verify(voteRepository, never()).delete(any(Vote.class));
        verify(voteRepository, never()).saveAndFlush(any(Vote.class));
    }

    @Test
    void voteForPostOrComment_withNonExistingPost_shouldDoNothing() {
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        voteService.voteForPostOrComment((byte) 1, UUID.randomUUID(), null, new User());

        verify(voteRepository, never()).delete(any(Vote.class));
        verify(voteRepository, never()).saveAndFlush(any(Vote.class));
    }

    @Test
    void voteForPostOrComment_withNullPostOrCommentIds_shouldDoNothing() {
        when(commentRepository.findById(any())).thenReturn(Optional.empty());
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        voteService.voteForPostOrComment((byte) 1, null, null, new User());

        verify(voteRepository, never()).delete(any(Vote.class));
        verify(voteRepository, never()).saveAndFlush(any(Vote.class));
    }

    @Test
    void findPostVotesByUser_shouldWorkCorrectly() {
        User user = mock(User.class);
        when(voteRepository.findPostVotesByUser(user)).thenReturn(createVotes(3));

        Map<String, Byte> response = voteService.findPostVotesByUser(user);
        assertEquals(3, response.size());
        response.forEach((k, v) -> {
            assertNotNull(k);
            assertNotNull(v);
        });
    }

    @Test
    void findPostVotesByUser_withNoVotes_returnsEmptyMap() {
        User user = mock(User.class);
        when(voteRepository.findPostVotesByUser(user)).thenReturn(createVotes(0));

        Map<String, Byte> response = voteService.findPostVotesByUser(user);
        assertTrue(response.isEmpty());
    }

    @Test
    void findCommentVotesByUser_shouldWorkCorrectly() {
        User user = mock(User.class);
        when(voteRepository.findCommentVotesByUser(user)).thenReturn(createVotes(3));

        Map<String, Byte> response = voteService.findCommentVotesByUser(user);
        assertEquals(3, response.size());
        response.forEach((k, v) -> {
            assertNotNull(k);
            assertNotNull(v);
        });
    }

    @Test
    void findCommentVotesByUser_withNoVotes_returnsEmptyMap() {
        User user = mock(User.class);
        when(voteRepository.findCommentVotesByUser(user)).thenReturn(createVotes(0));

        Map<String, Byte> response = voteService.findCommentVotesByUser(user);
        assertTrue(response.isEmpty());
    }

    @Test
    void getUserChoiceForPost_withUpvotedVote_returnsCorrectResponse() {
        Vote vote = mock(Vote.class);
        when(vote.getChoice()).thenReturn((byte) 1);
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(voteRepository.findByPostIdAndUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(vote));

        PostVoteUserChoiceResponse response = voteService.getUserChoiceForPost(user, UUID.randomUUID());

        assertTrue(response.isHasVoted());
        assertEquals((byte) 1, response.getChoice());
    }

    @Test
    void getUserChoiceForPost_withDownvotedVote_returnsCorrectResponse() {
        Vote vote = mock(Vote.class);
        when(vote.getChoice()).thenReturn((byte) -1);
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(voteRepository.findByPostIdAndUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(vote));

        PostVoteUserChoiceResponse response = voteService.getUserChoiceForPost(user, UUID.randomUUID());

        assertTrue(response.isHasVoted());
        assertEquals((byte) -1, response.getChoice());
    }

    @Test
    void getUserChoiceForPost_withNonExistingVote_returnsCorrectResponse() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(voteRepository.findByPostIdAndUserId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        PostVoteUserChoiceResponse response = voteService.getUserChoiceForPost(user, UUID.randomUUID());

        assertFalse(response.isHasVoted());
        assertNull(response.getChoice());
    }

    @Test
    void upvoteRandomPost_shouldCallRepositoryMethod() {
        voteService.upvoteRandomPost();
        verify(voteRepository, times(1)).upvoteRandomPost();
    }
}