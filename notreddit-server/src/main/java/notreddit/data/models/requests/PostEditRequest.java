package notreddit.data.models.requests;

import lombok.Getter;
import lombok.Setter;
import notreddit.constants.ErrorMessages;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class PostEditRequest {

    @NotNull
    private UUID postId;

    @Length(min = 4, message = ErrorMessages.TITLE_LENGTH_VIOLATION_MESSAGE)
    @NotBlank(message = ErrorMessages.BLANK_TITLE)
    private String title;

    private String url;

    private String content;

    @NotBlank(message = ErrorMessages.BLANK_SUBREDDIT)
    private String subreddit;

    private MultipartFile file;

    private boolean hasUploadedFile;
}
