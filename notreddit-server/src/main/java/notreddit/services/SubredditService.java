package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.SubredditCreateRequest;
import notreddit.domain.models.responses.subreddit.SubredditWithPostsAndSubscribersCountResponse;
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
