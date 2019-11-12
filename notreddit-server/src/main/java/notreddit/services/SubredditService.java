package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.SubredditCreateRequest;
import notreddit.domain.models.responses.subreddit.SubredditWithPostCountResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubredditService {

    ResponseEntity<?> create(SubredditCreateRequest request, User creator);

    Boolean existsByTitle(String title);

    List<String> getAllAsStrings();

    List<SubredditWithPostCountResponse> getAllWithPostCount();
}
