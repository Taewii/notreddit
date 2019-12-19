package notreddit.validations.entities;

import notreddit.data.entities.File;
import notreddit.data.entities.Post;
import notreddit.data.entities.Subreddit;
import notreddit.data.entities.User;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class PostValidationTests extends HibernateValidatorTest {

    private Post target;

    @BeforeEach
    public void setUp() {
        target = new Post();
    }

    @Test
    public void isValid() {
        target.setCreator(new User());
        target.setSubreddit(new Subreddit());
        target.setTitle("title");
        target.setContent("content");
        target.setUpvotes(1);
        target.setDownvotes(2);
        target.setFile(new File());
        target.setCreatedOn(LocalDateTime.now());

        isValid(target);
    }

    @Test
    public void isValid_withNullTitleAndFile() {
        target.setCreator(new User());
        target.setSubreddit(new Subreddit());
        target.setTitle("title");
        target.setContent(null);
        target.setFile(null);
        target.setCreatedOn(LocalDateTime.now());

        isValid(target);
    }

    @Test
    public void notValid_withNullCreator() {
        target.setCreator(null);
        target.setSubreddit(new Subreddit());
        target.setTitle("title");
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "creator", "must not be null");
    }

    @Test
    public void notValid_withNullSubreddit() {
        target.setCreator(new User());
        target.setSubreddit(null);
        target.setTitle("title");
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "subreddit", "must not be null");
    }

    @Test
    public void notValid_withBlankTitle() {
        target.setCreator(new User());
        target.setSubreddit(new Subreddit());
        target.setTitle("");
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "title", "must not be blank");
    }

    @Test
    public void notValid_withTitleLengthLessThanTheLimit() {
        target.setCreator(new User());
        target.setSubreddit(new Subreddit());
        target.setTitle("123");
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "title", "Title length must be more or equal to 4.");
    }

    @Test
    public void notValid_withNullCreatedOn() {
        target.setCreator(new User());
        target.setSubreddit(new Subreddit());
        target.setTitle("title");
        target.setCreatedOn(null);

        isInvalid(target);
        assertMessage(target, "createdOn", "must not be null");
    }

    @Test
    public void notValid_withCreatedOnDateThatIsInTheFuture() {
        target.setCreator(new User());
        target.setSubreddit(new Subreddit());
        target.setTitle("title");
        target.setCreatedOn(LocalDateTime.MAX);

        isInvalid(target);
        assertMessage(target, "createdOn", "must be a date in the past or in the present");
    }
}
