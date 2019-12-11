package notreddit.web.controllers;

import lombok.RequiredArgsConstructor;
import notreddit.domain.entities.User;
import notreddit.domain.models.responses.mention.MentionResponse;
import notreddit.services.MentionService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/mention")
@RequiredArgsConstructor
public class MentionController {

    private final MentionService mentionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unread-mentions-count")
    public int getUsersUnreadMentionsCount(@AuthenticationPrincipal User user) {
        return mentionService.getUnreadMentionCountByUser(user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user-mentions")
    public MentionResponse getUsersMentions(@AuthenticationPrincipal User user, Pageable pageable) {
        return mentionService.getMentionByUser(user, pageable);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/read")
    public ResponseEntity<?> markRead(@AuthenticationPrincipal User user,
                                      @RequestParam UUID mentionId) {
        return mentionService.mark(true, user, mentionId);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/unread")
    public ResponseEntity<?> markUnread(@AuthenticationPrincipal User user,
                                        @RequestParam UUID mentionId) {
        return mentionService.mark(false, user, mentionId);
    }
}
