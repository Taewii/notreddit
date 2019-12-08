package notreddit.validations.requests;

import notreddit.domain.models.requests.PostCreateRequest;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static notreddit.constants.ErrorMessages.BLANK_SUBREDDIT;
import static notreddit.constants.ErrorMessages.BLANK_TITLE;

public class PostCreateRequestValidationTests extends HibernateValidatorTest {

    private PostCreateRequest target;

    @BeforeEach
    public void setUp() {
        target = new PostCreateRequest();
    }

    @Test
    public void isValid() {
        target.setTitle("title");
        target.setUrl("url");
        target.setContent("content");
        target.setSubreddit("subreddit");
        target.setFile(new MockMultipartFile("file", new byte[0]));

        isValid(target);
    }

    @Test
    public void isValid_withNullUrlContentAndFile() {
        target.setTitle("title");
        target.setSubreddit("subreddit");
        target.setUrl(null);
        target.setContent(null);
        target.setFile(null);

        isValid(target);
    }

    @Test
    public void notValid_withBlankTitle() {
        target.setTitle("");
        target.setSubreddit("subreddit");

        isInvalid(target);
        assertMessage(target, "title", BLANK_TITLE);
    }

    @Test
    public void notValid_withTitleLengthUnderTheLimit() {
        target.setTitle("123");
        target.setSubreddit("subreddit");

        isInvalid(target);
        assertMessage(target, "title", "Title length must be more or equal to 4.");
    }

    @Test
    public void notValid_withBlankSubreddit() {
        target.setTitle("title");
        target.setSubreddit("");

        isInvalid(target);
        assertMessage(target, "subreddit", BLANK_SUBREDDIT);
    }
}
