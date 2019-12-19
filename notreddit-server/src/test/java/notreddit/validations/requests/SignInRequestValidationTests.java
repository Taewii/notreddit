package notreddit.validations.requests;

import notreddit.data.models.requests.SignInRequest;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static notreddit.constants.ErrorMessages.BLANK_PASSWORD;
import static notreddit.constants.ErrorMessages.BLANK_USERNAME;

public class SignInRequestValidationTests extends HibernateValidatorTest {

    private SignInRequest target;

    @BeforeEach
    public void setUp() {
        target = new SignInRequest();
    }

    @Test
    public void isValid() {
        target.setUsernameOrEmail("username");
        target.setPassword("password");

        isValid(target);
    }

    @Test
    public void notValid_withBlankUsername() {
        target.setUsernameOrEmail("");
        target.setPassword("password");

        isInvalid(target);
        assertMessage(target, "usernameOrEmail", BLANK_USERNAME);
    }

    @Test
    public void notValid_withBlankPassword() {
        target.setUsernameOrEmail("username");
        target.setPassword("");

        isInvalid(target);
        assertMessage(target, "password", BLANK_PASSWORD);
    }

    @Test
    public void notValid_withBlankPasswordAndBlankUsername() {
        target.setUsernameOrEmail("");
        target.setPassword("");

        isInvalid(target);
        assertMessage(target, "password", BLANK_PASSWORD);
        assertMessage(target, "usernameOrEmail", BLANK_USERNAME);
    }
}
