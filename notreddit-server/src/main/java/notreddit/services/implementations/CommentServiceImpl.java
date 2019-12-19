package notreddit.services.implementations;

import lombok.RequiredArgsConstructor;
import notreddit.data.entities.Comment;
import notreddit.data.entities.Mention;
import notreddit.data.entities.Post;
import notreddit.data.entities.User;
import notreddit.data.models.requests.CommentCreateRequest;
import notreddit.data.models.requests.CommentEditRequest;
import notreddit.data.models.responses.api.ApiResponse;
import notreddit.data.models.responses.comment.CommentListWithChildren;
import notreddit.data.models.responses.comment.CommentListWithReplyCount;
import notreddit.data.models.responses.comment.CommentsResponseModel;
import notreddit.repositories.CommentRepository;
import notreddit.repositories.MentionRepository;
import notreddit.repositories.PostRepository;
import notreddit.repositories.VoteRepository;
import notreddit.services.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static notreddit.constants.ApiResponseMessages.*;
import static notreddit.constants.ErrorMessages.ACCESS_FORBIDDEN;
import static notreddit.constants.GeneralConstants.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String MODERATOR_ROLE = "ROLE_MODERATOR";
    private static final String DELETED_CONTENT = "[deleted]";

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MentionRepository mentionRepository;
    private final VoteRepository voteRepository;
    private final ModelMapper mapper;

    @Override
    @Caching(evict = {
            @CacheEvict(value = COMMENTS_BY_POST_CACHE, allEntries = true),
            @CacheEvict(value = COMMENTS_BY_USERNAME, allEntries = true),
            @CacheEvict(value = POSTS_BY_USERNAME_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_SUBREDDIT_CACHE, allEntries = true),
            @CacheEvict(value = SUBSCRIBED_POSTS_CACHE, allEntries = true)
    })
    public ResponseEntity<?> create(CommentCreateRequest commentModel, User creator) {
        Post post = postRepository.findById(commentModel.getPostId()).orElse(null);
        Comment parent = null;

        if (post == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, NONEXISTENT_POST));
        }

        Comment comment = mapper.map(commentModel, Comment.class);
        comment.setCreator(creator);
        comment.setPost(post);
        comment.setCreatedOn(LocalDateTime.now());

        if (commentModel.getParentId() != null) {
            parent = commentRepository.findById(commentModel.getParentId()).orElse(null);
            comment.setParent(parent);

            if (parent != null) {
                Mention mention = createMention(comment, parent.getCreator(), creator);
                comment.getMentions().add(mention);
                parent.addChild(comment);
                commentRepository.saveAndFlush(parent);
            }
        }

        if (parent == null) {
            Mention mention = createMention(comment, post.getCreator(), creator);
            comment.getMentions().add(mention);
            commentRepository.saveAndFlush(comment);
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/comment/post")
                .buildAndExpand().toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true, SUCCESSFUL_COMMENT_CREATION));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = COMMENTS_BY_POST_CACHE, allEntries = true),
            @CacheEvict(value = COMMENTS_BY_USERNAME, allEntries = true)
    })
    public ResponseEntity<?> edit(CommentEditRequest commentModel, User user) {
        Comment comment = commentRepository.findById(commentModel.getCommentId()).orElse(null);

        if (comment == null || !user.getUsername().equals(comment.getCreator().getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, NONEXISTENT_COMMENT_OR_NOT_CREATOR));
        }

        comment.setContent(commentModel.getContent());
        commentRepository.saveAndFlush(comment);

        return ResponseEntity
                .ok(new ApiResponse(true, SUCCESSFUL_COMMENT_EDITING));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = COMMENTS_BY_POST_CACHE, allEntries = true),
            @CacheEvict(value = COMMENTS_BY_USERNAME, allEntries = true),
            @CacheEvict(value = POSTS_BY_USERNAME_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_SUBREDDIT_CACHE, allEntries = true),
            @CacheEvict(value = SUBSCRIBED_POSTS_CACHE, allEntries = true)
    })
    public ResponseEntity<?> delete(UUID commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, NONEXISTENT_COMMENT));
        }

        boolean isCreatorOrModerator = user.getUsername().equals(comment.getCreator().getUsername()) ||
                user.getRoles().stream().anyMatch(r -> r.getAuthority().equals(MODERATOR_ROLE));

        if (!isCreatorOrModerator) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, ACCESS_FORBIDDEN));
        }

        mentionRepository.deleteAllByCommentId(commentId);

        if (!comment.getChildren().isEmpty()) {
            comment.setContent(DELETED_CONTENT);
            commentRepository.saveAndFlush(comment);

            return ResponseEntity
                    .ok(new ApiResponse(true, SUCCESSFUL_COMMENT_DELETION));
        }

        voteRepository.deleteAllByCommentId(commentId);
        commentRepository.delete(comment);

        return ResponseEntity
                .ok(new ApiResponse(true, SUCCESSFUL_COMMENT_DELETION));
    }

    @Override
    @Cacheable(value = COMMENTS_BY_POST_CACHE, keyGenerator = "pageableKeyGenerator")
    public List<CommentListWithChildren> findAllFromPost(UUID postId, Pageable pageable) {
        return commentRepository
                .findByPostIdWithChildren(postId, pageable.getSort())
                .parallelStream()
                .map(c -> mapper.map(c, CommentListWithChildren.class))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = COMMENTS_BY_USERNAME, keyGenerator = "pageableKeyGenerator")
    public CommentsResponseModel findAllFromUsername(String username, Pageable pageable) {
        Page<Comment> byCreatorUsername = commentRepository.findByCreatorUsername(username.toLowerCase(), pageable);
        List<CommentListWithReplyCount> comments = byCreatorUsername.stream()
                .map(c -> {
                    CommentListWithReplyCount model = mapper.map(c, CommentListWithReplyCount.class);
                    model.setReplies(c.getChildren().size());
                    return model;
                }).collect(Collectors.toList());

        return new CommentsResponseModel(byCreatorUsername.getTotalElements(), comments);
    }

    private Mention createMention(Comment comment, User receiver, User creator) {
        Mention mention = new Mention();
        mention.setComment(comment);
        mention.setCreatedOn(LocalDateTime.now());
        mention.setCreator(creator);
        mention.setReceiver(receiver);

        return mention;
    }
}
