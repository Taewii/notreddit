package notreddit.domain.models.responses.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentsResponseModel {

    private long total;
    private List<? extends CommentListModel> comments;
}
