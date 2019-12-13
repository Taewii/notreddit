package notreddit.domain.models.responses.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostEditResponseModel {

    private String id;
    private String title;
    private String content;
    private String subredditTitle;
    private String fileUrl;
}
