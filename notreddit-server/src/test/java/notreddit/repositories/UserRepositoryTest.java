package notreddit.repositories;

import notreddit.PostgreSQLContainerInitializer;
import notreddit.data.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByIdWithRoles_shouldReturnUserAndHisRoles() {
        UUID userId = UUID.fromString("0cd5ebf9-1023-4164-81ad-e09e92f9cff2");
        Optional<User> byIdWithRoles = userRepository.findByIdWithRoles(userId);

        assertTrue(byIdWithRoles.isPresent());
        User user = byIdWithRoles.get();
        assertEquals(userId, user.getId());
        assertEquals(4, user.getAuthorities().size());
    }

    @Test
    void findByUsernameOrEmailIgnoreCase_shouldReturnTheSameUserBasedOnHisUsernameOrEmail() {
        Optional<User> byUsername = userRepository
                .findByUsernameOrEmailIgnoreCase("root", "randomEmail");
        Optional<User> byEmail = userRepository
                .findByUsernameOrEmailIgnoreCase("randomUsername", "email@abv.bg");

        assertTrue(byUsername.isPresent());
        assertTrue(byEmail.isPresent());
        assertEquals(byUsername.get(), byEmail.get());
    }

    @Test
    void existsByUsernameIgnoreCase_withExistingUser_returnsTrue() {
        Boolean exists = userRepository.existsByUsernameIgnoreCase("rOoT");
        assertTrue(exists);
    }

    @Test
    void existsByUsernameIgnoreCase_withNonExistentUser_returnsFalse() {
        Boolean exists = userRepository.existsByUsernameIgnoreCase("nonexistent");
        assertFalse(exists);
    }

    @Test
    void existsByEmailIgnoreCase_withExistingUser_returnsTrue() {
        Boolean exists = userRepository.existsByEmailIgnoreCase("emAil@aBv.BG");
        assertTrue(exists);
    }

    @Test
    void existsByEmailIgnoreCase_withNonExistentUser_returnsFalse() {
        Boolean exists = userRepository.existsByEmailIgnoreCase("nonexistent");
        assertFalse(exists);
    }

    @Test
    void findAllWithRoles_shouldReturnAllUsersWithTheirRespectiveRoles() {
        List<User> allWithRoles = userRepository.findAllWithRoles();

        assertEquals(4, allWithRoles.size());
        for (User user : allWithRoles) {
            int roleSize = user.getRoles().size();
            switch (user.getUsername()) {
                case "root":
                    assertEquals(4, roleSize);
                    break;
                case "admin":
                    assertEquals(3, roleSize);
                    break;
                case "moderator":
                    assertEquals(2, roleSize);
                    break;
                case "user":
                    assertEquals(1, roleSize);
                    break;
            }
        }
    }

    @Test
    void getWithSubscriptions_withValidData_shouldWorkCorrectly() {
        UUID userId = UUID.fromString("0cd5ebf9-1023-4164-81ad-e09e92f9cff2");
        User user = userRepository.findByIdWithRoles(userId).orElseThrow(NoSuchElementException::new);

        User withSubscriptions = userRepository.getWithSubscriptions(user);

        assertEquals(user, withSubscriptions);
        assertEquals(5, withSubscriptions.getSubscriptions().size());
    }
}