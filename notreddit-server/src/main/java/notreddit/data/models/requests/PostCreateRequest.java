package notreddit.data.models.requests;

import lombok.Getter;
import lombok.Setter;
import notreddit.constants.ErrorMessages;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PostCreateRequest {

    @Length(min = 4, message = ErrorMessages.TITLE_LENGTH_VIOLATION_MESSAGE)
    @NotBlank(message = ErrorMessages.BLANK_TITLE)
    private String title;

    private String url;

    private String content;

    @NotBlank(message = ErrorMessages.BLANK_SUBREDDIT)
    private String subreddit;

    private MultipartFile file;
}
