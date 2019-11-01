package notreddit.web.controllers;

import notreddit.domain.entities.User;
import notreddit.domain.models.responses.post.PostVoteUserChoiceResponse;
import notreddit.services.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/vote")
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/votes-posts")
    public Map<String, Byte> getCurrentUserVotesForPosts(@AuthenticationPrincipal User user) {
        return voteService.findPostVotesByUser(user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/votes-comments")
    public Map<String, Byte> getCurrentUserVotesForComments(@AuthenticationPrincipal User user) {
        return voteService.findCommentVotesByUser(user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post")
    public PostVoteUserChoiceResponse getUserVoteForPost(@RequestParam UUID postId,
                                                         @AuthenticationPrincipal User user) {
        return voteService.getUserChoiceForPost(user, postId);
    }
}
