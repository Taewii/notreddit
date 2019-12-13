package notreddit.validations.requests;

import notreddit.domain.models.requests.CommentCreateRequest;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static notreddit.constants.ErrorMessages.BLANK_CONTENT;
import static notreddit.constants.ErrorMessages.BLANK_POST_ID;

public class CommentCreateRequestValidationTests extends HibernateValidatorTest {

    private CommentCreateRequest target;

    @BeforeEach
    public void setUp() {
        target = new CommentCreateRequest();
    }

    @Test
    public void isValid() {
        target.setPostId(UUID.randomUUID());
        target.setContent("content");
        target.setParentId(UUID.randomUUID());

        isValid(target);
    }

    @Test
    public void isValid_withNullParentId() {
        target.setPostId(UUID.randomUUID());
        target.setContent("content");
        target.setParentId(null);

        isValid(target);
    }

    @Test
    public void notValid_withNullPostId() {
        target.setPostId(null);
        target.setContent("content");

        isInvalid(target);
        assertMessage(target, "postId", BLANK_POST_ID);
    }

    @Test
    public void notValid_withBlankContent() {
        target.setPostId(UUID.randomUUID());
        target.setContent("");

        isInvalid(target);
        assertMessage(target, "content", BLANK_CONTENT);
    }
}
