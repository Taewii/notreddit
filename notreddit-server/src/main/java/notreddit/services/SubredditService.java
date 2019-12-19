package notreddit.services;

import notreddit.data.entities.User;
import notreddit.data.models.requests.SubredditCreateRequest;
import notreddit.data.models.responses.subreddit.SubredditWithPostsAndSubscribersCountResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface SubredditService {

    Boolean existsByTitle(String title);

    Boolean isUserSubscribedToSubreddit(String subreddit, User user);

    ResponseEntity<?> create(SubredditCreateRequest request, User creator);

    ResponseEntity<?> subscribe(String subreddit, User user);

    ResponseEntity<?> unsubscribe(String subreddit, User user);

    List<String> getAllAsStrings();

    List<SubredditWithPostsAndSubscribersCountResponse> getAllWithPostCount();

    Set<String> getUserSubscriptions(User user);
}
