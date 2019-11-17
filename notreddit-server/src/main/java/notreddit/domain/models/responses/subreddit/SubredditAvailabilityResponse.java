package notreddit.domain.models.responses.subreddit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubredditAvailabilityResponse {

    private Boolean available;
}
