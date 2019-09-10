package notreddit.web.controllers;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.ChangeRoleRequest;
import notreddit.domain.models.responses.UserIdentityAvailabilityResponse;
import notreddit.domain.models.responses.UserSummaryResponse;
import notreddit.domain.models.responses.UsersResponse;
import notreddit.repositories.UserRepository;
import notreddit.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Autowired
    public UserController(UserService userService,
                          UserRepository userRepository,
                          ModelMapper mapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @GetMapping("/checkUsernameAvailability")
    public UserIdentityAvailabilityResponse checkUsernameAvailability(@RequestParam String username) {
        Boolean available = !userRepository.existsByUsername(username);
        return new UserIdentityAvailabilityResponse(available);
    }

    @GetMapping("/checkEmailAvailability")
    public UserIdentityAvailabilityResponse checkEmailAvailability(@RequestParam String email) {
        Boolean available = !userRepository.existsByEmail(email);
        return new UserIdentityAvailabilityResponse(available);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public UserSummaryResponse getCurrentUser(@AuthenticationPrincipal User user) {
        return mapper.map(user, UserSummaryResponse.class);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public UsersResponse getAllUsers() {
        List<UserSummaryResponse> users = userRepository.findAllWithRoles()
                .stream()
                .map(u -> mapper.map(u, UserSummaryResponse.class))
                .collect(Collectors.toUnmodifiableList());
        return new UsersResponse(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/changeRole")
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
}
