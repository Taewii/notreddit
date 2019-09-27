package notreddit.domain.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostListResponseModel {

    private UUID id;
    private String creatorUsername;
    private String title;
    private String fileThumbnailUrl;
    private String subredditTitle;
    private int upvotes;
    private int downvotes;
    private long createdAt;
    private int commentCount;
}
