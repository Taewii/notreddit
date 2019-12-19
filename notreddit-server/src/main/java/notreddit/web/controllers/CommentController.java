package notreddit.web.controllers;

import lombok.RequiredArgsConstructor;
import notreddit.data.entities.User;
import notreddit.data.models.requests.CommentCreateRequest;
import notreddit.data.models.requests.CommentEditRequest;
import notreddit.data.models.responses.comment.CommentListWithChildren;
import notreddit.data.models.responses.comment.CommentsResponseModel;
import notreddit.services.CommentService;
import notreddit.services.VoteService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final VoteService voteService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody CommentCreateRequest comment,
                                    @AuthenticationPrincipal User creator) {
        return commentService.create(comment, creator);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/edit")
    public ResponseEntity<?> edit(@Valid @RequestBody CommentEditRequest comment,
                                  @AuthenticationPrincipal User user) {
        return commentService.edit(comment, user);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam UUID commentId,
                                    @AuthenticationPrincipal User user) {
        return commentService.delete(commentId, user);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vote")
    public ResponseEntity<?> vote(@RequestParam byte choice,
                                  @RequestParam UUID commentId,
                                  @AuthenticationPrincipal User user) {
        return voteService.voteForPostOrComment(choice, null, commentId, user);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/post")
    public List<CommentListWithChildren> findAllFromPost(@RequestParam UUID postId, Pageable pageable) {
        return commentService.findAllFromPost(postId, pageable);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/user/{username}")
    public CommentsResponseModel findAllByUsername(@PathVariable String username, Pageable pageable) {
        return commentService.findAllFromUsername(username, pageable);
    }
}
