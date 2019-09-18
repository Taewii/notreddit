package notreddit.domain.models.requests;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PostCreateRequest {

    @Length(min = 4)
    @NotBlank
    private String title;

    private String url;
    private String content;
    private String subreddit;
    private MultipartFile file;
}
