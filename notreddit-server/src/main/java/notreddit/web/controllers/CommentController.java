package notreddit.web.controllers;

import com.weddini.throttling.Throttling;
import com.weddini.throttling.ThrottlingType;
import notreddit.domain.entities.User;
import notreddit.domain.models.requests.CommentPostRequestModel;
import notreddit.domain.models.responses.comment.CommentListWithChildren;
import notreddit.domain.models.responses.comment.CommentListWithReplyCount;
import notreddit.services.CommentService;
import notreddit.services.VoteService;
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
    private final VoteService voteService;

    @Autowired
    public CommentController(CommentService commentService,
                             VoteService voteService) {
        this.commentService = commentService;
        this.voteService = voteService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/post")
    public ResponseEntity<?> comment(@Valid @RequestBody CommentPostRequestModel comment,
                                     @AuthenticationPrincipal User creator) {
        return commentService.create(comment, creator);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/post")
    public List<CommentListWithChildren> findAllFromPost(@RequestParam UUID postId) {
        return commentService.findAllFromPost(postId);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/user/{username}")
    public List<CommentListWithReplyCount> findAllByUsername(@PathVariable String username) {
        return commentService.findAllFromUsername(username);
    }

    @Throttling(type = ThrottlingType.PrincipalName)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vote")
    public ResponseEntity<?> vote(@RequestParam byte choice,
                                  @RequestParam UUID commentId,
                                  @AuthenticationPrincipal User user) {
        return voteService.voteForPostOrComment(choice, null, commentId, user);
    }
}
