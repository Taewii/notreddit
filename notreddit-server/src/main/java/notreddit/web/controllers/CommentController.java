package notreddit.web.controllers;

import lombok.RequiredArgsConstructor;
import notreddit.domain.entities.User;
import notreddit.domain.models.requests.CommentCreateRequestModel;
import notreddit.domain.models.requests.CommentEditRequestModel;
import notreddit.domain.models.responses.comment.CommentListWithChildren;
import notreddit.domain.models.responses.comment.CommentsResponseModel;
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
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentCreateRequestModel comment,
                                           @AuthenticationPrincipal User creator) {
        return commentService.create(comment, creator);
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vote")
    public ResponseEntity<?> vote(@RequestParam byte choice,
                                  @RequestParam UUID commentId,
                                  @AuthenticationPrincipal User user) {
        return voteService.voteForPostOrComment(choice, null, commentId, user);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam UUID commentId,
                                    @AuthenticationPrincipal User user) {
        return commentService.delete(commentId, user);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/edit")
    public ResponseEntity<?> edit(@Valid @RequestBody CommentEditRequestModel comment,
                                  @AuthenticationPrincipal User user) {
        return commentService.edit(comment, user);
    }
}
