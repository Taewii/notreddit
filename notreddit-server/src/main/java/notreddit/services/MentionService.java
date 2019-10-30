package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.responses.mention.MentionResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface MentionService {

    int getUnreadMentionCountByUser(User user);

    MentionResponse getMentionByUser(User user, Pageable pageable);

    ResponseEntity<?> mark(boolean read, User user, UUID mentionId);
}
