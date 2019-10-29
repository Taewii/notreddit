package notreddit.domain.models.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentionResponseModel extends Identifiable {

    private String commentPostId;
    private String commentPostTitle;
    private String commentContent;
    private String creatorUsername;
    private boolean isRead;
    private long createdOn;
}
