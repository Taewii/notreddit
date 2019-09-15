package notreddit.web.controllers;

import notreddit.domain.models.requests.SubredditCreateRequest;
import notreddit.domain.models.responses.SubredditAvailabilityResponse;
import notreddit.repositories.SubredditRepository;
import notreddit.services.SubredditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import notreddit.domain.entities.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/subreddit")
public class SubredditController {

    private final SubredditRepository subredditRepository;
    private final SubredditService subredditService;

    @Autowired
    public SubredditController(SubredditRepository subredditRepository,
                               SubredditService subredditService) {
        this.subredditRepository = subredditRepository;
        this.subredditService = subredditService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/checkSubredditAvailability")
    public SubredditAvailabilityResponse checkUsernameAvailability(@RequestParam String title) {
        Boolean available = !subredditRepository.existsByTitle(title);
        return new SubredditAvailabilityResponse(available);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid SubredditCreateRequest request,
                                    @AuthenticationPrincipal User creator) {
        return subredditService.create(request, creator);
    }
}
