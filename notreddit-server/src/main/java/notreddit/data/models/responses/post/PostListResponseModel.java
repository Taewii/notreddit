package notreddit.data.models.responses.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostListResponseModel extends PostListModel {

    private String id;
    private int commentCount;
}
