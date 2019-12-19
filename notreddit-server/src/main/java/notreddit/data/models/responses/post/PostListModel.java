package notreddit.data.models.responses.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class PostListModel {

    private String creatorUsername;
    private String title;
    private String fileThumbnailUrl;
    private String subredditTitle;
    private int upvotes;
    private int downvotes;
    private long createdOn;
    private boolean creatorEnabled;
}
