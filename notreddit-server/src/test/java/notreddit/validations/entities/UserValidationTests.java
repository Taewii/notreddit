package notreddit.validations.entities;

import notreddit.domain.entities.Role;
import notreddit.domain.entities.User;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static notreddit.constants.ErrorMessages.INVALID_EMAIL;

public class UserValidationTests extends HibernateValidatorTest {

    private User target;

    @BeforeEach
    public void setUp() {
        target = new User();
    }

    @Test
    public void isValid() {
        target.setUsername("username");
        target.setPassword("password");
        target.setEmail("email@abv.bg");
        target.setRoles(Set.of(new Role()));

        isValid(target);
    }

    @Test
    public void notValid_withBlankUsername() {
        target.setUsername("");
        target.setPassword("password");
        target.setEmail("email@abv.bg");
        target.setRoles(Set.of(new Role()));

        isInvalid(target);
        assertMessage(target, "username", "must not be blank");
    }

    @Test
    public void notValid_withUsernameLengthLessThanTheLimit() {
        target.setUsername("123");
        target.setPassword("password");
        target.setEmail("email@abv.bg");
        target.setRoles(Set.of(new Role()));

        isInvalid(target);
        assertMessage(target, "username", "Username length must be more or equal to 4.");
    }

    @Test
    public void notValid_withBlankPassword() {
        target.setUsername("username");
        target.setPassword("");
        target.setEmail("email@abv.bg");
        target.setRoles(Set.of(new Role()));

        isInvalid(target);
        assertMessage(target, "password", "Password length must be more or equal to 6.");
    }

    @Test
    public void notValid_withBlankEmail() {
        target.setUsername("username");
        target.setPassword("password");
        target.setEmail("");
        target.setRoles(Set.of(new Role()));

        isInvalid(target);
        assertMessage(target, "email", "must not be blank");
    }

    @Test
    public void notValid_withInvalidEmail() {
        target.setUsername("username");
        target.setPassword("");
        target.setEmail("invalid");
        target.setRoles(Set.of(new Role()));

        isInvalid(target);
        assertMessage(target, "email", INVALID_EMAIL);
    }

    @Test
    public void notValid_withNoUserRoles() {
        target.setUsername("username");
        target.setPassword("password");
        target.setEmail("email@abv.bg");

        isInvalid(target);
        assertMessage(target, "roles", "must not be empty");
    }
}
