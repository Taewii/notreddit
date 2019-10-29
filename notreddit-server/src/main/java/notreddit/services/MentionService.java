package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.responses.MentionResponseModel;

import java.util.List;

public interface MentionService {

    int getUnreadMentionCountByUser(User user);

    List<MentionResponseModel> getMentionByUser(User user);
}
