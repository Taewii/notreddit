package notreddit.services.implementations;

import lombok.RequiredArgsConstructor;
import notreddit.constants.GeneralConstants;
import notreddit.domain.entities.Role;
import notreddit.domain.entities.Subreddit;
import notreddit.domain.entities.User;
import notreddit.domain.enums.Authority;
import notreddit.domain.models.requests.ChangeRoleRequest;
import notreddit.domain.models.requests.SignUpRequest;
import notreddit.domain.models.responses.api.ApiResponse;
import notreddit.domain.models.responses.user.UserSummaryResponse;
import notreddit.repositories.RoleRepository;
import notreddit.repositories.SubredditRepository;
import notreddit.repositories.UserRepository;
import notreddit.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static notreddit.constants.ApiResponseMessages.*;
import static notreddit.constants.ErrorMessages.ACCESS_FORBIDDEN;
import static notreddit.constants.GeneralConstants.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USERS_CACHE = "users";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SubredditRepository subredditRepository;
    private final PasswordEncoder encoder;
    private final ModelMapper mapper;

    @Override
    @Transactional
    @Cacheable(value = USERS_CACHE, key = "#id")
    public UserDetails loadUserById(UUID id) {
        return userRepository.findByIdWithRoles(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmailIgnoreCase(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException(NONEXISTENT_USERNAME_OR_EMAIL + usernameOrEmail)
                );
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    @Transactional
    public ResponseEntity<?> register(SignUpRequest model) {
        if (existsByUsername(model.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, USERNAME_IS_TAKEN));
        }

        if (existsByEmail(model.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, EMAIL_IS_TAKEN));
        }

        User user = mapper.map(model, User.class);
        user.setPassword(encoder.encode(model.getPassword()));

        if (userRepository.count() == 0) {
            user.setRoles(this.getInheritedRolesFromRole("ROOT"));
        } else {
            user.setRoles(this.getInheritedRolesFromRole("USER"));
        }

        Set<Subreddit> defaultSubreddits = subredditRepository
                .findByTitleIn(GeneralConstants.DEFAULT_SUBREDDITS);
        user.setSubscriptions(defaultSubreddits);

        userRepository.saveAndFlush(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/user/register")
                .buildAndExpand().toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true, SUCCESSFUL_USER_REGISTRATION));
    }

    @Override
    @CacheEvict(value = USERS_CACHE, key = "#request.userId")
    public ResponseEntity<?> changeRole(ChangeRoleRequest request, User user) {
        if (request.getNewRole().equalsIgnoreCase("root") ||
                request.getCurrentRole().equalsIgnoreCase("root")) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, CANNOT_CHANGE_ROLE_TO_FROM_ROOT));
        }

        boolean hasAdminRole = user.getAuthorities()
                .parallelStream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("role_admin"));

        if (!hasAdminRole) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, ACCESS_FORBIDDEN));
        }

        User affectedUser = userRepository.findByIdWithRoles(request.getUserId()).orElse(null);

        if (affectedUser == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, NONEXISTENT_USER_ID));
        }

        affectedUser.setRoles(this.getInheritedRolesFromRole(request.getNewRole()));
        userRepository.saveAndFlush(affectedUser);

        return ResponseEntity
                .ok()
                .body(new ApiResponse(true, String.format(CHANGED_USERS_ROLE_TO,
                        affectedUser.getUsername(), request.getNewRole())));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = POSTS_BY_ID_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_USERNAME_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_SUBREDDIT_CACHE, allEntries = true),
            @CacheEvict(value = SUBSCRIBED_POSTS_CACHE, allEntries = true),
            @CacheEvict(value = USERS_CACHE, key = "#userId"),
            @CacheEvict(value = COMMENTS_BY_POST_CACHE, allEntries = true),
            @CacheEvict(value = COMMENTS_BY_USERNAME, allEntries = true)
    })
    public ResponseEntity<?> deleteUser(String userId, User user) {
        boolean isRoot = user.getAuthorities()
                .parallelStream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("role_root"));

        if (!isRoot) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, ACCESS_FORBIDDEN));
        }

        User userToDelete = userRepository.findById(UUID.fromString(userId)).orElse(null);

        if (userToDelete == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, String.format(NONEXISTENT_USER_WITH_ID, userId)));
        }

        userToDelete.setEnabled(false);
        userRepository.saveAndFlush(userToDelete);
        return ResponseEntity
                .ok(new ApiResponse(true,
                        String.format(SUCCESSFUL_USER_DELETION, userToDelete.getUsername())));
    }

    @Override
    public List<UserSummaryResponse> findAllWithRoles() {
        return userRepository.findAllWithRoles()
                .stream()
                .map(u -> mapper.map(u, UserSummaryResponse.class))
                .collect(Collectors.toList());
    }

    private Set<Role> getInheritedRolesFromRole(String role) {
        Set<Role> roles = new HashSet<>();
        List<String> allRoles = roleRepository
                .findAll()
                .stream()
                .map(r -> r.getAuthority().substring(Authority.ROLE_PREFIX.length()))
                .collect(Collectors.toList());

        for (int i = allRoles.indexOf(role.toUpperCase()); i < allRoles.size(); i++) {
            roles.add(roleRepository.findById(i + 1L).orElseThrow(NoSuchElementException::new));
        }

        return roles;
    }
}
