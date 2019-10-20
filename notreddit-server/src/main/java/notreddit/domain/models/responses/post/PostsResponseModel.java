package notreddit.domain.models.responses.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostsResponseModel {

    private long total;
    private List<? extends PostListModel> posts;
}
