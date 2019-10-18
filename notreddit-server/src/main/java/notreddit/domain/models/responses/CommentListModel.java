package notreddit.domain.models.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
abstract class CommentListModel {

    private String id;
    private String creatorUsername;
    private String content;
    private int upvotes;
    private int downvotes;
    private long createdOn;
}
