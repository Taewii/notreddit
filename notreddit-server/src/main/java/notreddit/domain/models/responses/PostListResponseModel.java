package notreddit.domain.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostListResponseModel {

    private UUID id;
    private String creator;
    private String title;
    private String url;
    private int upvotes;
    private int downvotes;
    private LocalDateTime createdOn;
    private int commentCount;
}
