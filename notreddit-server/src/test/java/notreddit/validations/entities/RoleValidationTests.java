package notreddit.validations.entities;

import notreddit.data.entities.Role;
import notreddit.data.enums.Authority;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RoleValidationTests extends HibernateValidatorTest {

    private Role target;

    @BeforeEach
    public void setUp() {
        target = new Role();
    }

    @Test
    public void isValid() {
        target.setAuthority(Authority.USER);

        isValid(target);
    }

    @Test
    public void notValid_withNullAuthority() {
        target.setAuthority(null);

        isInvalid(target);
        assertMessage(target, "authority", "must not be null");
    }
}
