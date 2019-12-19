package notreddit.validations.requests;

import notreddit.data.models.requests.PostEditRequest;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static notreddit.constants.ErrorMessages.BLANK_SUBREDDIT;
import static notreddit.constants.ErrorMessages.BLANK_TITLE;

public class PostEditRequestValidationTests extends HibernateValidatorTest {

    private PostEditRequest target;

    @BeforeEach
    public void setUp() {
        target = new PostEditRequest();
    }

    @Test
    public void isValid() {
        target.setPostId(UUID.randomUUID());
        target.setTitle("title");
        target.setUrl("url");
        target.setContent("content");
        target.setSubreddit("subreddit");
        target.setFile(new MockMultipartFile("file", new byte[0]));

        isValid(target);
    }

    @Test
    public void isValid_withNullUrlContentAndFile() {
        target.setPostId(UUID.randomUUID());
        target.setTitle("title");
        target.setSubreddit("subreddit");
        target.setUrl(null);
        target.setContent(null);
        target.setFile(null);

        isValid(target);
    }

    @Test
    public void notValid_withNullPostId() {
        target.setPostId(null);
        target.setTitle("");
        target.setSubreddit("subreddit");

        isInvalid(target);
        assertMessage(target, "postId", "must not be null");
    }

    @Test
    public void notValid_withBlankTitle() {
        target.setPostId(UUID.randomUUID());
        target.setTitle("");
        target.setSubreddit("subreddit");

        isInvalid(target);
        assertMessage(target, "title", BLANK_TITLE);
    }

    @Test
    public void notValid_withTitleLengthUnderTheLimit() {
        target.setPostId(UUID.randomUUID());
        target.setTitle("123");
        target.setSubreddit("subreddit");

        isInvalid(target);
        assertMessage(target, "title", "Title length must be more or equal to 4.");
    }

    @Test
    public void notValid_withBlankSubreddit() {
        target.setPostId(UUID.randomUUID());
        target.setTitle("title");
        target.setSubreddit("");

        isInvalid(target);
        assertMessage(target, "subreddit", BLANK_SUBREDDIT);
    }
}
