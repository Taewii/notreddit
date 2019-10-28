package notreddit.services;

import notreddit.domain.entities.User;

public interface MentionService {

    int getUnreadMentionCountByUser(User user);
}
