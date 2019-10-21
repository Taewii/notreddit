package notreddit.domain.models.responses.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentListWithReplyCount extends CommentListModel {

    private int replies;
    private String postTitle;
    private String postId;
}
