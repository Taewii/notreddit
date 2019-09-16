package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.SubredditCreateRequest;
import org.springframework.http.ResponseEntity;

public interface SubredditService {

    ResponseEntity<?> create(SubredditCreateRequest request, User creator);

    Boolean existsByTitle(String title);
}
