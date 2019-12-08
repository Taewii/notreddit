package notreddit.validations.entities;

import notreddit.domain.entities.Comment;
import notreddit.domain.entities.Post;
import notreddit.domain.entities.User;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class CommentValidationTests extends HibernateValidatorTest {

    private Comment target;

    @BeforeEach
    public void setUp() {
        target = new Comment();
    }

    @Test
    public void isValid() {
        target.setCreator(new User());
        target.setPost(new Post());
        target.setContent("content");
        target.setUpvotes(1);
        target.setDownvotes(1);
        target.setParent(new Comment());
        target.setCreatedOn(LocalDateTime.now());

        isValid(target);
    }

    @Test
    public void isValid_withNullParent() {
        target.setCreator(new User());
        target.setPost(new Post());
        target.setContent("content");
        target.setCreatedOn(LocalDateTime.now());

        isValid(target);
    }

    @Test
    public void notValid_withNullCreator() {
        target.setCreator(null);
        target.setPost(new Post());
        target.setContent("content");
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "creator", "must not be null");
    }

    @Test
    public void notValid_withNullPost() {
        target.setCreator(new User());
        target.setPost(null);
        target.setContent("content");
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "post", "must not be null");
    }

    @Test
    public void notValid_withBlankContent() {
        target.setCreator(new User());
        target.setPost(new Post());
        target.setContent("");
        target.setCreatedOn(LocalDateTime.now());

        isInvalid(target);
        assertMessage(target, "content", "must not be blank");
    }

    @Test
    public void notValid_withNullCreatedOn() {
        target.setCreator(new User());
        target.setPost(new Post());
        target.setContent("content");
        target.setCreatedOn(null);

        isInvalid(target);
        assertMessage(target, "createdOn", "must not be null");
    }

    @Test
    public void notValid_withCreatedOnDateThatIsInTheFuture() {
        target.setCreator(new User());
        target.setPost(new Post());
        target.setContent("content");
        target.setCreatedOn(LocalDateTime.MAX);

        isInvalid(target);
        assertMessage(target, "createdOn", "must be a date in the past or in the present");
    }
}
