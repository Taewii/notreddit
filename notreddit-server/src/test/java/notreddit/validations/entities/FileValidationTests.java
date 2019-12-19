package notreddit.validations.entities;

import notreddit.data.entities.File;
import notreddit.data.entities.Post;
import notreddit.validations.HibernateValidatorTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class FileValidationTests extends HibernateValidatorTest {

    private File target;

    @BeforeEach
    public void setUp() {
        target = new File();
    }

    @Test
    public void isValid() {
        target.setId(UUID.randomUUID());
        target.setUrl("url");
        target.setThumbnailUrl("thumbnailUrl");
        target.setPost(new Post());

        isValid(target);
    }

    @Test
    public void isValid_withNullThumbnailUrl() {
        target.setId(UUID.randomUUID());
        target.setUrl("url");
        target.setThumbnailUrl(null);
        target.setPost(new Post());

        isValid(target);
    }

    @Test
    public void notValid_withBlankUrl() {
        target.setId(UUID.randomUUID());
        target.setUrl("");
        target.setPost(new Post());

        isInvalid(target);
        assertMessage(target, "url", "must not be blank");
    }

    @Test
    public void notValid_withNullPost() {
        target.setId(UUID.randomUUID());
        target.setUrl("url");
        target.setPost(null);

        isInvalid(target);
        assertMessage(target, "post", "must not be null");
    }
}
