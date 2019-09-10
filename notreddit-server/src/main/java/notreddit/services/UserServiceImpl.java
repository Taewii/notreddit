package notreddit.services;

import notreddit.domain.entities.Role;
import notreddit.domain.entities.User;
import notreddit.domain.models.requests.ChangeRoleRequest;
import notreddit.domain.models.requests.SignUpRequest;
import notreddit.domain.models.responses.ApiResponse;
import notreddit.repositories.RoleRepository;
import notreddit.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class UserServiceImpl implements UserService {

    private static final List<String> ROLES = new ArrayList<>() {{
        add("ROOT");
        add("ADMIN");
        add("MODERATOR");
        add("USER");
    }};

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final ModelMapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder encoder,
                           ModelMapper mapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail)
                );
    }

    @Override
    @Transactional
    public UserDetails loadUserById(UUID id) {
        return userRepository.findByIdWithRoles(id).orElseThrow();
    }

    @Override
    public ResponseEntity<?> register(SignUpRequest model) {
        if (userRepository.existsByUsername(model.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Username is already taken!"));
        }

        if (userRepository.existsByEmail(model.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Email Address already in use!"));
        }

        User user = mapper.map(model, User.class);
        user.setPassword(encoder.encode(model.getPassword()));

        if (userRepository.count() == 0) {
            user.setRoles(this.getInheritedRolesFromRole("ROOT"));
        } else {
            user.setRoles(this.getInheritedRolesFromRole("USER"));
        }

        userRepository.saveAndFlush(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/home")
                .buildAndExpand().toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true, "User registered successfully"));
    }

    @Override
    public ResponseEntity<?> changeRole(ChangeRoleRequest request, User user) {
        if (request.getNewRole().equalsIgnoreCase("root") ||
                request.getCurrentRole().equalsIgnoreCase("root")) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Cannot change role form/to ROOT"));
        }

        boolean hasAdminRole = user.getAuthorities()
                .stream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("role_admin"));

        if (!hasAdminRole) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "You don't have permissions to do that."));
        }

        User affectedUser = userRepository.findByIdWithRoles(request.getUserId()).orElseThrow();
        affectedUser.setRoles(this.getInheritedRolesFromRole(request.getNewRole()));
        userRepository.saveAndFlush(affectedUser);

        return ResponseEntity
                .ok()
                .body(new ApiResponse(true, String.format("Changed %s's role to %s",
                        affectedUser.getUsername(), request.getNewRole())));
    }

    @Override
    public ResponseEntity<?> deleteUser(String userId, User user) {
        boolean isRoot = user.getAuthorities()
                .stream()
                .anyMatch(r -> r.getAuthority().equalsIgnoreCase("role_root"));

        if (!isRoot) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "You don't have permissions to do that."));
        }

        User userToDelete = userRepository.findById(UUID.fromString(userId)).orElse(null);

        if (userToDelete == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, String.format("User with id: %s doesn't exist", userId)));
        }

        userRepository.delete(userToDelete);
        return ResponseEntity
                .ok()
                .body(new ApiResponse(true,
                        String.format("User %s deleted successfully.", userToDelete.getUsername())));
    }

    private Set<Role> getInheritedRolesFromRole(String role) {
        Set<Role> roles = new HashSet<>();
        for (int i = ROLES.indexOf(role.toUpperCase()); i < ROLES.size(); i++) {
            roles.add(roleRepository.findById(i + 1L).orElseThrow());
        }

        return roles;
    }
}
