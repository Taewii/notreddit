package notreddit.domain.models.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommentListWithChildrenResponse {

    private String id;
    private String creatorUsername;
    private String content;
    private int upvotes;
    private int downvotes;
    private long createdOn;
    private List<CommentListWithChildrenResponse> children = new ArrayList<>();
}
