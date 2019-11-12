package notreddit.web.controllers;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.SubredditCreateRequest;
import notreddit.domain.models.responses.subreddit.SubredditAvailabilityResponse;
import notreddit.domain.models.responses.subreddit.SubredditWithPostCountResponse;
import notreddit.services.SubredditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/subreddit")
public class SubredditController {

    private final SubredditService subredditService;

    @Autowired
    public SubredditController(SubredditService subredditService) {
        this.subredditService = subredditService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/checkSubredditAvailability")
    public SubredditAvailabilityResponse checkNameAvailability(@RequestParam String title) {
        Boolean available = !subredditService.existsByTitle(title);
        return new SubredditAvailabilityResponse(available);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid SubredditCreateRequest request,
                                    @AuthenticationPrincipal User creator) {
        return subredditService.create(request, creator);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public List<String> getAllSubredditNames() {
        return subredditService.getAllAsStrings();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all-with-post-count")
    public List<SubredditWithPostCountResponse> getAllSubredditsWithPostCount() {
        return subredditService.getAllWithPostCount();
    }
}
