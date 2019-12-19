package notreddit.validations.entities;

import notreddit.data.entities.Comment;
import notreddit.data.entities.Post;
import notreddit.data.entities.User;
import notreddit.data.entities.Vote;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class VoteValidationTests extends HibernateValidatorTest {

    private Vote target;

    @BeforeEach
    public void setUp() {
        target = new Vote();
    }

    @Test
    public void isValid() {
        target.setChoice((byte) 1);
        target.setUser(new User());
        target.setCreatedOn(LocalDateTime.now());
        target.setComment(new Comment());
        target.setPost(new Post());

        isValid(target);
    }

    @Test
    public void isValid_withNullCommentAndPost() {
        target.setChoice((byte) 1);
        target.setUser(new User());
        target.setCreatedOn(LocalDateTime.now());
        target.setComment(null);
        target.setPost(null);

        isValid(target);
    }

    @Test
    public void notValid_withChoiceUnderTheLimit() {
        target.setChoice((byte) -2);
        target.setUser(new User());
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "choice", "must be greater than or equal to -1");
    }

    @Test
    public void notValid_withChoiceOverTheLimit() {
        target.setChoice((byte) 2);
        target.setUser(new User());
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "choice", "must be less than or equal to 1");
    }

    @Test
    public void notValid_withNullUser() {
        target.setChoice((byte) 1);
        target.setUser(null);
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "user", "must not be null");
    }

    @Test
    public void notValid_withNullCreatedOn() {
        target.setChoice((byte) 1);
        target.setUser(new User());
        target.setCreatedOn(null);

        isInvalid(target);
        assertMessage(target, "createdOn", "must not be null");
    }

    @Test
    public void notValid_withCreatedOnDateThatIsInTheFuture() {
        target.setChoice((byte) 1);
        target.setUser(new User());
        target.setCreatedOn(LocalDateTime.MAX);

        isInvalid(target);
        assertMessage(target, "createdOn", "must be a date in the past or in the present");
    }
}
