package notreddit.services;

import notreddit.data.entities.User;
import notreddit.data.models.requests.ChangeRoleRequest;
import notreddit.data.models.requests.SignUpRequest;
import notreddit.data.models.responses.user.UserSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    UserDetails loadUserById(UUID id);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    ResponseEntity<?> register(SignUpRequest model);

    ResponseEntity<?> changeRole(ChangeRoleRequest request, User user);

    ResponseEntity<?> deleteUser(String userId, User user);

    List<UserSummaryResponse> findAllWithRoles();
}
