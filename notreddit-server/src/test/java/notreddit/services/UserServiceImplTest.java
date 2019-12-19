package notreddit.services;

import notreddit.SingletonModelMapper;
import notreddit.data.entities.Role;
import notreddit.data.entities.User;
import notreddit.data.enums.Authority;
import notreddit.data.models.requests.ChangeRoleRequest;
import notreddit.data.models.requests.SignUpRequest;
import notreddit.data.models.responses.user.UserSummaryResponse;
import notreddit.repositories.RoleRepository;
import notreddit.repositories.SubredditRepository;
import notreddit.repositories.UserRepository;
import notreddit.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserService userService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SubredditRepository subredditRepository;

    private List<Role> getRoles() {
        Role rootRole = new Role();
        rootRole.setId(1L);
        rootRole.setAuthority(Authority.ROOT);
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setAuthority(Authority.ADMIN);
        Role modRole = new Role();
        modRole.setId(3L);
        modRole.setAuthority(Authority.MODERATOR);
        Role userRole = new Role();
        userRole.setId(4L);
        userRole.setAuthority(Authority.USER);

        return Arrays.asList(rootRole, adminRole, modRole, userRole);
    }

    private List<User> createUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setUsername("username" + i);
            user.setRoles(new HashSet<>(getRoles()));
            users.add(user);
        }

        return users;
    }

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        subredditRepository = mock(SubredditRepository.class);
        userService = new UserServiceImpl(
                userRepository,
                roleRepository,
                subredditRepository,
                new BCryptPasswordEncoder(),
                SingletonModelMapper.mapper());
    }

    @Test
    void loadUserByUsername_withExistingUser_shouldWorkCorrectly() {
        when(userRepository.findByUsernameOrEmailIgnoreCase(any(String.class), any(String.class)))
                .thenReturn(Optional.of(new User()));
        String str = "usernameOrEmail";
        userService.loadUserByUsername(str);
        verify(userRepository).findByUsernameOrEmailIgnoreCase(str, str);
    }

    @Test
    void loadUserByUsername_withNonExistingUser_shouldThrowUsernameNotFoundException() {
        when(userRepository.findByIdWithRoles(any(UUID.class))).thenReturn(Optional.empty());
        String str = "usernameOrEmail";
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(str));
        verify(userRepository).findByUsernameOrEmailIgnoreCase(str, str);
    }

    @Test
    void loadUserById_withExistingUser_shouldWorkCorrectly() {
        when(userRepository.findByIdWithRoles(any(UUID.class))).thenReturn(Optional.of(new User()));
        UUID id = UUID.randomUUID();
        userService.loadUserById(id);
        verify(userRepository).findByIdWithRoles(id);
    }

    @Test
    void loadUserById_withNonExistingUser_shouldThrowNoSuchElementException() {
        when(userRepository.findByIdWithRoles(any(UUID.class))).thenReturn(Optional.empty());
        UUID id = UUID.randomUUID();
        Assertions.assertThrows(NoSuchElementException.class, () -> userService.loadUserById(id));
        verify(userRepository).findByIdWithRoles(id);
    }

    @Test
    void register_withValidDataAndNoUsers_createsNewUserWithRootRolesAndDefaultSubreddits() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("username");
        request.setEmail("email");
        request.setPassword("password");
        request.setConfirmPassword("password");

        List<Role> roleList = getRoles();
        when(roleRepository.findAll()).thenReturn(roleList);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(roleList.get(0)));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(roleList.get(1)));
        when(roleRepository.findById(3L)).thenReturn(Optional.of(roleList.get(2)));
        when(roleRepository.findById(4L)).thenReturn(Optional.of(roleList.get(3)));

        when(userRepository.existsByEmailIgnoreCase(any(String.class))).thenReturn(false);
        when(userRepository.existsByUsernameIgnoreCase(any(String.class))).thenReturn(false);
        when(userRepository.count()).thenReturn(0L);
        when(subredditRepository.findByTitleIn(any())).thenReturn(new HashSet<>());

        ResponseEntity<?> result = userService.register(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(subredditRepository).findByTitleIn(any());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void register_withValidDataAndExistingUsers_createsNewUserWithUserRoleAndDefaultSubreddits() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("username");
        request.setEmail("email");
        request.setPassword("password");
        request.setConfirmPassword("password");

        List<Role> roleList = getRoles();
        when(roleRepository.findAll()).thenReturn(roleList);
        when(roleRepository.findById(4L)).thenReturn(Optional.of(roleList.get(3)));
        // if it gives and exception here it tried to access more than just the User role

        when(userRepository.existsByEmailIgnoreCase(any(String.class))).thenReturn(false);
        when(userRepository.existsByUsernameIgnoreCase(any(String.class))).thenReturn(false);
        when(userRepository.count()).thenReturn(1L);
        when(subredditRepository.findByTitleIn(any())).thenReturn(new HashSet<>());

        ResponseEntity<?> result = userService.register(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(subredditRepository).findByTitleIn(any());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void register_withExistingUsername_shouldDoNothing() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("username");
        request.setEmail("email");

        when(userRepository.existsByUsernameIgnoreCase(any(String.class))).thenReturn(true);
        when(userRepository.existsByEmailIgnoreCase(any(String.class))).thenReturn(false);

        userService.register(request);

        verify(subredditRepository, never()).findByTitleIn(any());
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void register_withExistingEmail_shouldDoNothing() {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("username");
        request.setEmail("email");

        when(userRepository.existsByEmailIgnoreCase(any(String.class))).thenReturn(true);
        when(userRepository.existsByUsernameIgnoreCase(any(String.class))).thenReturn(false);

        userService.register(request);

        verify(subredditRepository, never()).findByTitleIn(any());
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void changeRole_withAdminUserAndValidData_shouldGiveTheCorrectRolesToTheNewUser() {
        User root = createUsers(1).get(0);
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setCurrentRole("USER");
        request.setNewRole("MODERATOR");
        request.setUserId(UUID.randomUUID());

        List<Role> roleList = getRoles();

        when(roleRepository.findAll()).thenReturn(roleList);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(roleList.get(0)));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(roleList.get(1)));
        when(roleRepository.findById(3L)).thenReturn(Optional.of(roleList.get(2)));
        when(roleRepository.findById(4L)).thenReturn(Optional.of(roleList.get(3)));

        User user = mock(User.class);
        when(userRepository.findByIdWithRoles(any(UUID.class))).thenReturn(Optional.of(user));

        ArgumentCaptor<Set<Role>> roleCaptor = ArgumentCaptor.forClass(Set.class);

        userService.changeRole(request, root);

        verify(user).setRoles(roleCaptor.capture());
        verify(userRepository).saveAndFlush(user);
        Set<String> roles = roleCaptor.getValue().parallelStream().map(Role::getAuthority).collect(Collectors.toSet());
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_MODERATOR"));
        assertTrue(roles.contains("ROLE_USER"));
    }

    @Test
    public void changeRole_withUserThatIsNotAdmin_shouldDoNothing() {
        User user = createUsers(1).get(0);
        user.setRoles(new HashSet<>());
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setCurrentRole("USER");
        request.setNewRole("MODERATOR");

        userService.changeRole(request, user);

        verify(userRepository, never()).findByIdWithRoles(any(UUID.class));
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    public void changeRole_withAdminUserButNonExistingNewUser_shouldDoNothing() {
        User user = createUsers(1).get(0);
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setCurrentRole("USER");
        request.setNewRole("MODERATOR");
        request.setUserId(UUID.randomUUID());
        when(userRepository.findByIdWithRoles(any(UUID.class))).thenReturn(Optional.empty());

        userService.changeRole(request, user);

        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    public void changeRole_withUserCurrentRoleBeingRoot_shouldDoNothing() {
        User user = createUsers(1).get(0);
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setCurrentRole("ROOT");
        request.setNewRole("MODERATOR");

        userService.changeRole(request, user);

        verify(userRepository, never()).findByIdWithRoles(any(UUID.class));
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    public void changeRole_withUserNewRoleBeingRoot_shouldDoNothing() {
        User user = createUsers(1).get(0);
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setCurrentRole("ADMIN");
        request.setNewRole("ROOT");

        userService.changeRole(request, user);

        verify(userRepository, never()).findByIdWithRoles(any(UUID.class));
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void deleteUser_withRootUser_shouldDeleteSuccessfully() {
        User user = createUsers(1).get(0);
        User userToDelete = mock(User.class);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(userToDelete));
        userService.deleteUser(UUID.randomUUID().toString(), user);

        verify(userRepository).saveAndFlush(userToDelete);
    }

    @Test
    void deleteUser_withRootUserAndNonExistentUserToDelete_shouldDoNothing() {
        User user = createUsers(1).get(0);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        userService.deleteUser(UUID.randomUUID().toString(), user);

        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void deleteUser_withModeratorUser_shouldDoNothing() {
        User user = createUsers(1).get(0);
        user.setRoles(new HashSet<>());
        userService.deleteUser(UUID.randomUUID().toString(), user);

        verify(userRepository, never()).findById(any(UUID.class));
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void existsByUsername_withExistingUser_returnsTrue() {
        when(userRepository.existsByUsernameIgnoreCase(any(String.class))).thenReturn(true);
        Boolean result = userService.existsByUsername("username");
        assertTrue(result);
        verify(userRepository).existsByUsernameIgnoreCase("username");
    }

    @Test
    void existsByUsername_withNonExistingUser_returnsFalse() {
        when(userRepository.existsByUsernameIgnoreCase(any(String.class))).thenReturn(false);
        Boolean result = userService.existsByUsername("username");
        assertFalse(result);
        verify(userRepository).existsByUsernameIgnoreCase("username");
    }

    @Test
    void existsByEmail_withExistingUser_returnsTrue() {
        when(userRepository.existsByEmailIgnoreCase(any(String.class))).thenReturn(true);
        Boolean result = userService.existsByEmail("email");
        assertTrue(result);
        verify(userRepository).existsByEmailIgnoreCase("email");
    }

    @Test
    void existsByEmail_withNonExistingUser_returnsFalse() {
        when(userRepository.existsByEmailIgnoreCase(any(String.class))).thenReturn(false);
        Boolean result = userService.existsByEmail("email");
        assertFalse(result);
        verify(userRepository).existsByEmailIgnoreCase("email");
    }

    @Test
    void findAllWithRoles_withExistingUsers_returnsCorrectData() {
        when(userRepository.findAllWithRoles()).thenReturn(createUsers(3));

        List<UserSummaryResponse> response = userService.findAllWithRoles();

        assertEquals(3, response.size());
        for (int i = 0; i < response.size(); i++) {
            assertEquals("username" + i, response.get(i).getUsername());
            assertTrue(response.get(i).getRoles().contains("ROOT"));
            assertTrue(response.get(i).getRoles().contains("ADMIN"));
            assertTrue(response.get(i).getRoles().contains("MODERATOR"));
            assertTrue(response.get(i).getRoles().contains("USER"));
        }
    }

    @Test
    void findAllWithRoles_withNoExistingUsers_returnsEmptyList() {
        when(userRepository.findAllWithRoles()).thenReturn(new ArrayList<>());
        List<UserSummaryResponse> response = userService.findAllWithRoles();
        assertTrue(response.isEmpty());
    }
}