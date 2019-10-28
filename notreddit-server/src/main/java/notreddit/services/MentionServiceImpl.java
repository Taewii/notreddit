package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.repositories.MentionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MentionServiceImpl implements MentionService {

    private final MentionRepository mentionRepository;

    @Autowired
    public MentionServiceImpl(MentionRepository mentionRepository) {
        this.mentionRepository = mentionRepository;
    }

    @Override
    public int getUnreadMentionCountByUser(User user) {
        return mentionRepository.getUnreadMentionCountByUser(user);
    }
}
