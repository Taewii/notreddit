package notreddit.validations.requests;

import notreddit.domain.models.requests.SubredditCreateRequest;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static notreddit.constants.ErrorMessages.BLANK_TITLE;

public class SubredditCreateRequestValidationTests extends HibernateValidatorTest {

    private SubredditCreateRequest target;

    @BeforeEach
    public void setUp() {
        target = new SubredditCreateRequest();
    }

    @Test
    public void isValid() {
        target.setTitle("title");

        isValid(target);
    }

    @Test
    public void notValid_withBlankTitle() {
        target.setTitle("");

        isInvalid(target);
        assertMessage(target, "title", BLANK_TITLE);
    }

    @Test
    public void notValid_withTitleLengthUnderTheLimit() {
        target.setTitle("12");

        isInvalid(target);
        assertMessage(target, "title", "Title length must be more or equal to 3.");
    }
}
