package notreddit.web.controllers;

import lombok.RequiredArgsConstructor;
import notreddit.data.entities.User;
import notreddit.data.models.responses.post.PostVoteUserChoiceResponse;
import notreddit.services.VoteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/vote")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

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
