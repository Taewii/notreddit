package notreddit.data.models.responses.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDetailsResponseModel extends PostListModel {

    private String content;
    private String fileUrl;
}
