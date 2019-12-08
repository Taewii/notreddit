package notreddit.validations.requests;

import notreddit.domain.models.requests.ChangeRoleRequest;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static notreddit.constants.ErrorMessages.*;

public class ChangeRoleRequestValidationTests extends HibernateValidatorTest {

    private ChangeRoleRequest target;

    @BeforeEach
    public void setUp() {
        target = new ChangeRoleRequest();
    }

    @Test
    public void isValid() {
        target.setUserId(UUID.randomUUID());
        target.setCurrentRole("ADMIN");
        target.setNewRole("USER");

        isValid(target);
    }

    @Test
    public void notValid_withNullUserId() {
        target.setUserId(null);
        target.setCurrentRole("ADMIN");
        target.setNewRole("USER");

        isInvalid(target);
        assertMessage(target, "userId", BLANK_USER_ID);
    }

    @Test
    public void notValid_withBlankCurrentRole() {
        target.setUserId(UUID.randomUUID());
        target.setCurrentRole("");
        target.setNewRole("USER");

        isInvalid(target);
        assertMessage(target, "currentRole", BLANK_CURRENT_ROLE);
    }

    @Test
    public void notValid_withBlankNewRole() {
        target.setUserId(UUID.randomUUID());
        target.setCurrentRole("ADMIN");
        target.setNewRole("");

        isInvalid(target);
        assertMessage(target, "newRole", BLANK_NEW_ROLE);
    }
}
