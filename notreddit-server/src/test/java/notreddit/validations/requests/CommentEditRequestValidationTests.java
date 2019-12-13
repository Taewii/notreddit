package notreddit.validations.requests;

import notreddit.domain.models.requests.CommentEditRequest;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static notreddit.constants.ErrorMessages.BLANK_COMMENT_ID;
import static notreddit.constants.ErrorMessages.BLANK_CONTENT;

public class CommentEditRequestValidationTests extends HibernateValidatorTest {

    private CommentEditRequest target;

    @BeforeEach
    public void setUp() {
        target = new CommentEditRequest();
    }

    @Test
    public void isValid() {
        target.setCommentId(UUID.randomUUID());
        target.setContent("content");

        isValid(target);
    }

    @Test
    public void notValid_withNullCommentId() {
        target.setCommentId(null);
        target.setContent("content");

        isInvalid(target);
        assertMessage(target, "commentId", BLANK_COMMENT_ID);
    }

    @Test
    public void notValid_withBlankContent() {
        target.setCommentId(UUID.randomUUID());
        target.setContent("");

        isInvalid(target);
        assertMessage(target, "content", BLANK_CONTENT);
    }
}
