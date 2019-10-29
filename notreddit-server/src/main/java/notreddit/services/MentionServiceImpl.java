package notreddit.services;

import notreddit.domain.entities.User;
import notreddit.domain.models.responses.MentionResponseModel;
import notreddit.repositories.MentionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentionServiceImpl implements MentionService {

    private final MentionRepository mentionRepository;
    private final ModelMapper mapper;

    @Autowired
    public MentionServiceImpl(MentionRepository mentionRepository,
                              ModelMapper mapper) {
        this.mentionRepository = mentionRepository;
        this.mapper = mapper;
    }

    @Override
    public int getUnreadMentionCountByUser(User user) {
        return mentionRepository.getUnreadMentionCountByUser(user);
    }

    @Override
    public List<MentionResponseModel> getMentionByUser(User user) {
        return mentionRepository.getUsersMentions(user)
                .parallelStream()
                .map(m -> mapper.map(m, MentionResponseModel.class))
                .collect(Collectors.toUnmodifiableList());
    }
}
