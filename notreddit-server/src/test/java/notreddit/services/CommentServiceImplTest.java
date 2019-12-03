package notreddit.services;

import notreddit.SingletonModelMapper;
import notreddit.domain.entities.Comment;
import notreddit.domain.entities.Post;
import notreddit.domain.entities.Role;
import notreddit.domain.entities.User;
import notreddit.domain.enums.Authority;
import notreddit.domain.models.requests.CommentCreateRequestModel;
import notreddit.domain.models.requests.CommentEditRequestModel;
import notreddit.domain.models.responses.comment.CommentListWithChildren;
import notreddit.domain.models.responses.comment.CommentsResponseModel;
import notreddit.repositories.CommentRepository;
import notreddit.repositories.MentionRepository;
import notreddit.repositories.PostRepository;
import notreddit.repositories.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    private CommentService commentService;
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private MentionRepository mentionRepository;
    private VoteRepository voteRepository;

    @BeforeEach
    void setUp() {
        commentRepository = mock(CommentRepository.class);
        postRepository = mock(PostRepository.class);
        mentionRepository = mock(MentionRepository.class);
        voteRepository = mock(VoteRepository.class);
        commentService = new CommentServiceImpl(
                postRepository,
                commentRepository,
                mentionRepository,
                voteRepository,
                SingletonModelMapper.mapper());
    }

    private Page<Comment> createComments(int count, Pageable pageable) {
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setUsername("username" + i);

            Post post = new Post();
            post.setId(UUID.randomUUID());
            post.setTitle("title" + i);

            Comment comment = new Comment();
            comment.setId(UUID.randomUUID());
            comment.setCreator(user);
            comment.setContent("content" + i);
            comment.setUpvotes(i);
            comment.setDownvotes(i);
            comment.setCreatedOn(LocalDateTime.now());
            comment.setChildren(comments);
            comments.add(comment);
        }

        return new PageImpl<>(comments, pageable, comments.size());
    }

    @Test
    void create_withParentComment_shouldAddCommentToParentAndSave() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");
        Post post = mock(Post.class);
        when(post.getCreator()).thenReturn(user);
        Comment comment = mock(Comment.class);

        CommentCreateRequestModel model = mock(CommentCreateRequestModel.class);
        UUID parentId = UUID.randomUUID();
        when(model.getParentId()).thenReturn(parentId);
        when(model.getPostId()).thenReturn(UUID.randomUUID());

        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        try {
            commentService.create(model, user);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        verify(comment).addChild(any(Comment.class));
        verify(commentRepository).findById(parentId);
        verify(commentRepository).saveAndFlush(comment);
    }

    @Test
    void create_withNoParentComment_shouldSaveComment() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");
        Post post = mock(Post.class);
        when(post.getCreator()).thenReturn(user);

        CommentCreateRequestModel model = mock(CommentCreateRequestModel.class);
        when(model.getParentId()).thenReturn(null);
        when(model.getPostId()).thenReturn(UUID.randomUUID());

        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        try {
            commentService.create(model, user);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        verify(commentRepository).saveAndFlush(any(Comment.class));
        verify(commentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void create_withNoSuchPost_shouldDoNothing() {
        CommentCreateRequestModel model = mock(CommentCreateRequestModel.class);
        when(model.getPostId()).thenReturn(UUID.randomUUID());
        when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        commentService.create(model, new User());

        verify(commentRepository, never()).saveAndFlush(any(Comment.class));
        verify(commentRepository, never()).findById(any(UUID.class));
    }

    @Test
    void findAllFromPost_shouldWorkCorrectly() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(commentRepository.findByPostIdWithChildren(any(UUID.class), any(Sort.class)))
                .thenReturn(createComments(3, pageable).getContent());

        List<CommentListWithChildren> result = commentService.findAllFromPost(UUID.randomUUID(), pageable);

        assertEquals(3, result.size());
        assertEquals(CommentListWithChildren.class, result.get(0).getClass());
        for (int i = 0; i < result.size(); i++) {
            assertEquals("username" + i, result.get(i).getCreatorUsername());
            assertEquals("content" + i, result.get(i).getContent());
            assertEquals(i, result.get(i).getUpvotes());
            assertEquals(i, result.get(i).getDownvotes());
            assertFalse(result.get(i).getChildren().isEmpty());
        }
    }

    @Test
    void findAllFromPost_withNoComments_returnsEmptyList() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(commentRepository.findByPostIdWithChildren(any(UUID.class), any(Sort.class)))
                .thenReturn(new ArrayList<>());

        List<CommentListWithChildren> result = commentService.findAllFromPost(UUID.randomUUID(), pageable);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllFromUsername_shouldWorkCorrectly() {
        Pageable pageable = PageRequest.of(0, 3);
        when(commentRepository.findByCreatorUsername(any(String.class), any(Pageable.class)))
                .thenReturn(createComments(3, pageable));

        CommentsResponseModel response = commentService.findAllFromUsername("username", pageable);

        assertEquals(3, response.getComments().size());
        assertEquals(3, response.getTotal());
    }

    @Test
    void findAllFromUsername_withNoComments_returnsEmptyResponse() {
        Pageable pageable = PageRequest.of(0, 3);
        when(commentRepository.findByCreatorUsername(any(String.class), any(Pageable.class)))
                .thenReturn(createComments(0, pageable));

        CommentsResponseModel response = commentService.findAllFromUsername("username", pageable);

        assertTrue(response.getComments().isEmpty());
        assertEquals(0, response.getTotal());
    }

    @Test
    void delete_withModeratorUserAndNoCommentChildren_deletesEverythingCorrectly() {
        Role mod = new Role();
        mod.setAuthority(Authority.MODERATOR);

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("random");
        when(user.getRoles()).thenReturn(Set.of(mod));

        User creator = mock(User.class);
        when(user.getUsername()).thenReturn("creator");

        Comment comment = mock(Comment.class);
        when(comment.getCreator()).thenReturn(creator);
        when(comment.getChildren()).thenReturn(new ArrayList<>());

        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        commentService.delete(UUID.randomUUID(), user);

        verify(mentionRepository).deleteAllByCommentId(any(UUID.class));
        verify(voteRepository).deleteAllByCommentId(any(UUID.class));
        verify(commentRepository).delete(comment);

        verify(comment, never()).setContent(any(String.class));
        verify(commentRepository, never()).saveAndFlush(any(Comment.class));
    }

    @Test
    void delete_withModeratorUserAndCommentChildren_deletesMentionsAndChangesCommentContent() {
        Role mod = new Role();
        mod.setAuthority(Authority.MODERATOR);

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("random");
        when(user.getRoles()).thenReturn(Set.of(mod));

        User creator = mock(User.class);
        when(user.getUsername()).thenReturn("creator");

        Comment comment = mock(Comment.class);
        when(comment.getCreator()).thenReturn(creator);
        when(comment.getChildren()).thenReturn(List.of(new Comment()));

        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        commentService.delete(UUID.randomUUID(), user);

        verify(mentionRepository).deleteAllByCommentId(any(UUID.class));
        verify(comment).setContent("[deleted]");
        verify(commentRepository).saveAndFlush(comment);

        verify(voteRepository, never()).deleteAllByCommentId(any(UUID.class));
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void delete_withCreatorUserAndNoCommentChildren_deletesEverythingCorrectly() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");

        Comment comment = mock(Comment.class);
        when(comment.getCreator()).thenReturn(user);
        when(comment.getChildren()).thenReturn(new ArrayList<>());

        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        commentService.delete(UUID.randomUUID(), user);

        verify(mentionRepository).deleteAllByCommentId(any(UUID.class));
        verify(voteRepository).deleteAllByCommentId(any(UUID.class));
        verify(commentRepository).delete(comment);

        verify(comment, never()).setContent(any(String.class));
        verify(commentRepository, never()).saveAndFlush(any(Comment.class));
    }

    @Test
    void delete_withCreatorUserAndCommentChildren_deletesMentionsAndChangesCommentContent() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");

        Comment comment = mock(Comment.class);
        when(comment.getCreator()).thenReturn(user);
        when(comment.getChildren()).thenReturn(List.of(new Comment()));

        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        commentService.delete(UUID.randomUUID(), user);

        verify(mentionRepository).deleteAllByCommentId(any(UUID.class));
        verify(comment).setContent("[deleted]");
        verify(commentRepository).saveAndFlush(comment);

        verify(voteRepository, never()).deleteAllByCommentId(any(UUID.class));
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void delete_withUserNotBeingTheCreatorOrModerator_shouldDoNothing() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");

        User creator = mock(User.class);
        when(user.getUsername()).thenReturn("creator");

        Comment comment = mock(Comment.class);
        when(comment.getCreator()).thenReturn(creator);
        when(comment.getChildren()).thenReturn(new ArrayList<>());

        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        commentService.delete(UUID.randomUUID(), user);

        verify(mentionRepository, never()).deleteAllByCommentId(any(UUID.class));
        verify(comment, never()).setContent("[deleted]");
        verify(commentRepository, never()).saveAndFlush(comment);

        verify(voteRepository, never()).deleteAllByCommentId(any(UUID.class));
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void delete_withNonExistingPost_shouldDoNothing() {
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        commentService.delete(UUID.randomUUID(), new User());

        verify(mentionRepository, never()).deleteAllByCommentId(any(UUID.class));
        verify(commentRepository, never()).saveAndFlush(any(Comment.class));
        verify(voteRepository, never()).deleteAllByCommentId(any(UUID.class));
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void edit_withValidData_shouldWorkCorrectly() {
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");

        Comment comment = mock(Comment.class);
        when(comment.getCreator()).thenReturn(user);

        CommentEditRequestModel request = new CommentEditRequestModel();
        request.setCommentId(UUID.randomUUID());
        request.setContent("new content");

        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        commentService.edit(request, user);

        verify(comment).setContent("new content");
        verify(commentRepository).saveAndFlush(comment);
    }

    @Test
    void edit_withNonExistingComment_shouldDoNothing() {
        CommentEditRequestModel request = new CommentEditRequestModel();
        request.setCommentId(UUID.randomUUID());
        request.setContent("new content");
        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        commentService.edit(request, new User());

        verify(commentRepository, never()).saveAndFlush(any());
    }

    @Test
    void edit_withUserThatIsNotTheCreator_shouldDoNothing() {
        User currentUser = mock(User.class);
        when(currentUser.getUsername()).thenReturn("username");

        User creator = mock(User.class);
        when(creator.getUsername()).thenReturn("creator");

        Comment comment = mock(Comment.class);
        when(comment.getCreator()).thenReturn(creator);

        CommentEditRequestModel request = new CommentEditRequestModel();
        request.setCommentId(UUID.randomUUID());

        when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.of(comment));

        commentService.edit(request, currentUser);

        verify(comment, never()).setContent("new content");
        verify(commentRepository, never()).saveAndFlush(comment);
    }
}