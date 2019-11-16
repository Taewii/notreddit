package notreddit.domain.models.responses.subreddit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IsUserSubscribedToSubredditResponse {

    private Boolean isSubscribed;
}
