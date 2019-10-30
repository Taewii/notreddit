package notreddit.domain.models.responses.mention;

import lombok.Getter;
import lombok.Setter;
import notreddit.domain.models.responses.Identifiable;

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
