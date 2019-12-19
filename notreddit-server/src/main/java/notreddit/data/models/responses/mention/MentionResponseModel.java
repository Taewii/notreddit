package notreddit.data.models.responses.mention;

import lombok.Getter;
import lombok.Setter;
import notreddit.data.models.responses.Identifiable;

@Getter
@Setter
public class MentionResponseModel extends Identifiable {

    private String commentPostId;
    private String commentPostTitle;
    private String commentContent;
    private String creatorUsername;
    private boolean isRead;
    private long createdOn;
    private boolean creatorEnabled;
}
