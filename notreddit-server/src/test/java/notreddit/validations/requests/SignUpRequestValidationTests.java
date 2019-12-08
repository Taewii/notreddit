package notreddit.validations.requests;

import notreddit.domain.models.requests.SignUpRequest;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static notreddit.constants.ErrorMessages.*;

public class SignUpRequestValidationTests extends HibernateValidatorTest {

    private SignUpRequest target;

    @BeforeEach
    public void setUp() {
        target = new SignUpRequest();
    }

    @Test
    public void isValid() {
        target.setUsername("username");
        target.setPassword("password");
        target.setConfirmPassword("password");
        target.setEmail("email@abv.bg");

        isValid(target);
    }

    @Test
    public void notValid_withBlankUsername() {
        target.setUsername("");
        target.setPassword("password");
        target.setConfirmPassword("password");
        target.setEmail("email@abv.bg");

        isInvalid(target);
        assertMessage(target, "username", BLANK_USERNAME);
    }

    @Test
    public void notValid_withBlankPassword() {
        target.setUsername("username");
        target.setPassword("");
        target.setConfirmPassword("password");
        target.setEmail("email@abv.bg");

        isInvalid(target);
        assertMessage(target, "password", BLANK_PASSWORD);
    }

    @Test
    public void notValid_withBlankConfirmPassword() {
        target.setUsername("username");
        target.setPassword("password");
        target.setConfirmPassword("");
        target.setEmail("email@abv.bg");

        isInvalid(target);
        assertMessage(target, "confirmPassword", BLANK_CONFIRM_PASSWORD);
    }

    @Test
    public void notValid_withBlankEmail() {
        target.setUsername("username");
        target.setPassword("password");
        target.setConfirmPassword("password");
        target.setEmail("");

        isInvalid(target);
        assertMessage(target, "email", BLANK_EMAIL);
    }

    @Test
    public void notValid_withUsernameLengthUnderTheLimit() {
        target.setUsername("123");
        target.setPassword("password");
        target.setConfirmPassword("password");
        target.setEmail("email@abv.bg");

        isInvalid(target);
        assertMessage(target, "username", "Username length must be more or equal to 4.");
    }

    @Test
    public void notValid_withPasswordLengthUnderTheLimit() {
        target.setUsername("username");
        target.setPassword("12345");
        target.setConfirmPassword("password");
        target.setEmail("email@abv.bg");

        isInvalid(target);
        assertMessage(target, "password", "Password length must be more or equal to 6.");
    }

    @Test
    public void notValid_withConfirmPasswordLengthUnderTheLimit() {
        target.setUsername("username");
        target.setPassword("password");
        target.setConfirmPassword("12345");
        target.setEmail("email@abv.bg");

        isInvalid(target);
        assertMessage(target, "confirmPassword", "Confirm password length must be more or equal to 6.");
    }

    @Test
    public void notValid_withInvalidEmail() {
        target.setUsername("username");
        target.setPassword("password");
        target.setConfirmPassword("password");
        target.setEmail("invalidEmail");

        isInvalid(target);
        assertMessage(target, "email", INVALID_EMAIL);
    }

    @Test
    public void notValid_withPasswordAndConfirmPasswordFieldsNotMatching() {
        target.setUsername("username");
        target.setPassword("somePassword");
        target.setConfirmPassword("otherPassword");
        target.setEmail("invalidEmail");

        isInvalid(target);
        assertMessage(target, "password", FIELDS_ARE_NOT_MATCHING);
        assertMessage(target, "confirmPassword", FIELDS_ARE_NOT_MATCHING);

    }
}
