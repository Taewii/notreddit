package notreddit.web.controllers;

import notreddit.domain.entities.User;
import notreddit.domain.models.responses.MentionResponseModel;
import notreddit.services.MentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mention")
public class MentionController {

    private final MentionService mentionService;

    @Autowired
    public MentionController(MentionService mentionService) {
        this.mentionService = mentionService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unread-mentions-count")
    public int getUsersUnreadMentionsCount(@AuthenticationPrincipal User user) {
        return mentionService.getUnreadMentionCountByUser(user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user-mentions")
    public List<MentionResponseModel> getUsersMentions(@AuthenticationPrincipal User user) {
        return mentionService.getMentionByUser(user);
    }
}
