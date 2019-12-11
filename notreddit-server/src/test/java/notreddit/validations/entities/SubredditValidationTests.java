package notreddit.validations.entities;

import notreddit.domain.entities.Subreddit;
import notreddit.domain.entities.User;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubredditValidationTests extends HibernateValidatorTest {

    private Subreddit target;

    @BeforeEach
    public void setUp() {
        target = new Subreddit();
    }

    @Test
    public void isValid() {
        target.setTitle("title");
        target.setCreator(new User());

        isValid(target);
    }

    @Test
    public void isValid_withNullCreator() {
        target.setTitle("title");
        target.setCreator(null);

        isValid(target);
    }

    @Test
    public void notValid_withBlankTitle() {
        target.setTitle("");
        target.setCreator(new User());

        isInvalid(target);
        assertMessage(target, "title", "must not be blank");
    }

    @Test
    public void notValid_withTitleLengthUnderTheLimit() {
        target.setTitle("12");
        target.setCreator(new User());

        isInvalid(target);
        assertMessage(target, "title", "Title length must be more or equal to 3.");
    }
}
