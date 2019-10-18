package notreddit.domain.models.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentListWithReplyCount extends CommentListModel {

    private int replies;
}
