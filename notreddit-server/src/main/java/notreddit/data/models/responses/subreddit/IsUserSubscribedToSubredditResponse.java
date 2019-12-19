package notreddit.data.models.responses.subreddit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IsUserSubscribedToSubredditResponse {

    private Boolean isSubscribed;
}
