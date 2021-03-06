package notreddit.web.controllers;

import lombok.RequiredArgsConstructor;
import notreddit.data.entities.User;
import notreddit.data.models.requests.SubredditCreateRequest;
import notreddit.data.models.responses.subreddit.IsUserSubscribedToSubredditResponse;
import notreddit.data.models.responses.subreddit.SubredditAvailabilityResponse;
import notreddit.data.models.responses.subreddit.SubredditWithPostsAndSubscribersCountResponse;
import notreddit.services.SubredditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/subreddit")
@RequiredArgsConstructor
public class SubredditController {

    private final SubredditService subredditService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid SubredditCreateRequest request,
                                    @AuthenticationPrincipal User creator) {
        return subredditService.create(request, creator);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestParam String subreddit,
                                       @AuthenticationPrincipal User user) {
        return subredditService.subscribe(subreddit, user);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestParam String subreddit,
                                         @AuthenticationPrincipal User user) {
        return subredditService.unsubscribe(subreddit, user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/check-subreddit-availability")
    public SubredditAvailabilityResponse checkNameAvailability(@RequestParam String title) {
        Boolean available = !subredditService.existsByTitle(title);
        return new SubredditAvailabilityResponse(available);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/is-subscribed")
    public IsUserSubscribedToSubredditResponse isUserSubscribedToSubreddit(@RequestParam String subreddit,
                                                                           @AuthenticationPrincipal User user) {
        Boolean isSubscribed = subredditService.isUserSubscribedToSubreddit(subreddit, user);
        return new IsUserSubscribedToSubredditResponse(isSubscribed);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public List<String> getAllSubredditNames() {
        return subredditService.getAllAsStrings();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/all-with-post-count")
    public List<SubredditWithPostsAndSubscribersCountResponse> getAllSubredditsWithPostCount() {
        return subredditService.getAllWithPostCount();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/subscriptions")
    public Set<String> getUserSubscriptions(@AuthenticationPrincipal User user) {
        return subredditService.getUserSubscriptions(user);
    }
}
