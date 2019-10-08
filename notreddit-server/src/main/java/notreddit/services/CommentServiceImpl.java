package notreddit.services;

import notreddit.domain.entities.Comment;
import notreddit.domain.entities.Post;
import notreddit.domain.entities.User;
import notreddit.domain.models.requests.CommentPostRequestModel;
import notreddit.domain.models.responses.ApiResponse;
import notreddit.repositories.CommentRepository;
import notreddit.repositories.PostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper mapper;

    @Autowired
    public CommentServiceImpl(PostRepository postRepository,
                              CommentRepository commentRepository,
                              ModelMapper mapper) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<?> create(CommentPostRequestModel commentModel, User creator) {
        Post post = postRepository.findById(commentModel.getPostId()).orElse(null);
        Comment parent = null;

        if (post == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Post doesn't exist."));
        }

        if (commentModel.getParentId() != null) {
            parent = commentRepository.findById(commentModel.getParentId()).orElse(null);
        }

        Comment comment = mapper.map(commentModel, Comment.class);
        comment.setParent(parent);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());

        commentRepository.saveAndFlush(comment);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/comment/post")
                .buildAndExpand().toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true, "Comment created successfully."));
    }
}
