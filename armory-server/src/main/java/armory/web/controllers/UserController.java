package armory.web.controllers;

import armory.domain.entities.User;
import armory.domain.models.responses.UserIdentityAvailabilityResponse;
import armory.domain.models.responses.UserSummaryResponse;
import armory.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Autowired
    public UserController(UserRepository userRepository,
                          ModelMapper mapper) {
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
}
