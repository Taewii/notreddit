package notreddit.domain.models.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDetailsResponseModel {

    private String creatorUsername;
    private String title;
    private String fileThumbnailUrl;
    private String fileUrl;
    private String subredditTitle;
    private int upvotes;
    private int downvotes;
    private long createdAt;
}
