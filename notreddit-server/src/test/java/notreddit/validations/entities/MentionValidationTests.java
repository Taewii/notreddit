package notreddit.validations.entities;

import notreddit.domain.entities.Comment;
import notreddit.domain.entities.Mention;
import notreddit.domain.entities.User;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class MentionValidationTests extends HibernateValidatorTest {

    private Mention target;

    @BeforeEach
    public void setUp() {
        target = new Mention();
    }

    @Test
    public void isValid() {
        target.setCreator(new User());
        target.setReceiver(new User());
        target.setComment(new Comment());
        target.setRead(true);
        target.setCreatedOn(LocalDateTime.now());

        isValid(target);
    }

    @Test
    public void notValid_withNullCreator() {
        target.setCreator(null);
        target.setReceiver(new User());
        target.setComment(new Comment());
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "creator", "must not be null");
    }

    @Test
    public void notValid_withNullReceiver() {
        target.setCreator(new User());
        target.setReceiver(null);
        target.setComment(new Comment());
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "receiver", "must not be null");
    }

    @Test
    public void notValid_withNullComment() {
        target.setCreator(new User());
        target.setReceiver(new User());
        target.setComment(null);
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "comment", "must not be null");
    }

    @Test
    public void notValid_withNullCreatedOn() {
        target.setCreator(new User());
        target.setReceiver(new User());
        target.setComment(new Comment());
        target.setCreatedOn(null);

        isInvalid(target);
        assertMessage(target, "createdOn", "must not be null");
    }

    @Test
    public void notValid_withCreatedOnDateThatIsInTheFuture() {
        target.setCreator(new User());
        target.setReceiver(new User());
        target.setComment(new Comment());
        target.setCreatedOn(LocalDateTime.MAX);

        isInvalid(target);
        assertMessage(target, "createdOn", "must be a date in the past or in the present");
    }
}
