package notreddit.domain.models.responses.subreddit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubredditWithPostCountResponse {

    private String title;
    private Integer postCount;
}
