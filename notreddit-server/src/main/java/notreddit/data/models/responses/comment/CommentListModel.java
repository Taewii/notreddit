package notreddit.data.models.responses.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import notreddit.data.models.responses.Identifiable;

@Getter
@Setter
@NoArgsConstructor
abstract class CommentListModel extends Identifiable {

    private String creatorUsername;
    private String content;
    private int upvotes;
    private int downvotes;
    private long createdOn;
    private boolean creatorEnabled;
}
