package notreddit.web.controllers;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.CommentPostRequestModel;
import notreddit.domain.models.responses.CommentListWithChildrenResponse;
import notreddit.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/post")
    public ResponseEntity<?> comment(@Valid @RequestBody CommentPostRequestModel comment,
                                     @AuthenticationPrincipal User creator) {
        return commentService.create(comment, creator);
    }

    @GetMapping("/post")
    public List<CommentListWithChildrenResponse> findAllFromPost(@RequestParam UUID postId) {
        return commentService.findAllFromPost(postId);
    }
}
