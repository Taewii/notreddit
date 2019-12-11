package notreddit.web.controllers;

import lombok.RequiredArgsConstructor;
import notreddit.domain.entities.User;
import notreddit.domain.models.requests.ChangeRoleRequest;
import notreddit.domain.models.responses.post.PostsResponseModel;
import notreddit.domain.models.responses.user.UserIdentityAvailabilityResponse;
import notreddit.domain.models.responses.user.UserSummaryResponse;
import notreddit.domain.models.responses.user.UsersResponse;
import notreddit.services.PostService;
import notreddit.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final ModelMapper mapper;

    @PreAuthorize("isAnonymous()")
    @GetMapping("/check-username-availability")
    public UserIdentityAvailabilityResponse checkUsernameAvailability(@RequestParam String username) {
        Boolean available = !userService.existsByUsername(username);
        return new UserIdentityAvailabilityResponse(available);
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/check-email-availability")
    public UserIdentityAvailabilityResponse checkEmailAvailability(@RequestParam String email) {
        Boolean available = !userService.existsByEmail(email);
        return new UserIdentityAvailabilityResponse(available);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public UserSummaryResponse getCurrentUser(@AuthenticationPrincipal User user) {
        return mapper.map(user, UserSummaryResponse.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public UsersResponse getAllUsers() {
        List<UserSummaryResponse> users = userService.findAllWithRoles();
        return new UsersResponse(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/change-role")
    public ResponseEntity<?> changeUserRole(@Valid @RequestBody ChangeRoleRequest request,
                                            @AuthenticationPrincipal User user) {
        return userService.changeRole(request, user);
    }

    @PreAuthorize("hasRole('ROOT')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String id,
                                        @AuthenticationPrincipal User user) {
        return userService.deleteUser(id, user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{username}/upvoted")
    public PostsResponseModel getUpvotedPosts(@AuthenticationPrincipal User user,
                                              @PathVariable String username,
                                              Pageable pageable) {
        return postService.getPostsByVoteChoice(user, username, 1, pageable);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{username}/downvoted")
    public PostsResponseModel getDownvotedPosts(@AuthenticationPrincipal User user,
                                                @PathVariable String username,
                                                Pageable pageable) {
        return postService.getPostsByVoteChoice(user, username, -1, pageable);
    }
}
