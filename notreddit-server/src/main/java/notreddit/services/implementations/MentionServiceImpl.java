package notreddit.services.implementations;

import lombok.RequiredArgsConstructor;
import notreddit.data.entities.Mention;
import notreddit.data.entities.User;
import notreddit.data.models.responses.api.ApiResponse;
import notreddit.data.models.responses.mention.MentionResponse;
import notreddit.data.models.responses.mention.MentionResponseModel;
import notreddit.repositories.MentionRepository;
import notreddit.services.MentionService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static notreddit.constants.ApiResponseMessages.MENTION_MARKED_AS;
import static notreddit.constants.ApiResponseMessages.NONEXISTENT_MENTION_OR_NOT_RECEIVER;

@Service
@RequiredArgsConstructor
public class MentionServiceImpl implements MentionService {

    private final MentionRepository mentionRepository;
    private final ModelMapper mapper;

    @Override
    public int getUnreadMentionCountByUser(User user) {
        return mentionRepository.getUnreadMentionCountByUser(user);
    }

    @Override
    public ResponseEntity<?> mark(boolean read, User user, UUID mentionId) {
        Mention mention = mentionRepository.findByIdWithReceiver(mentionId).orElse(null);

        if (mention == null || !user.getUsername().equals(mention.getReceiver().getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, NONEXISTENT_MENTION_OR_NOT_RECEIVER));
        }

        mention.setRead(read);
        mentionRepository.saveAndFlush(mention);

        String message = String.format(MENTION_MARKED_AS, read ? "read" : "unread");
        return ResponseEntity
                .ok()
                .body(new ApiResponse(true, message));
    }

    @Override
    public MentionResponse getMentionByUser(User user, Pageable pageable) {
        Page<Mention> usersMentions = mentionRepository.getUsersMentions(user, pageable);
        List<MentionResponseModel> mentions = usersMentions.stream()
                .map(m -> mapper.map(m, MentionResponseModel.class))
                .collect(Collectors.toList());

        return new MentionResponse(usersMentions.getTotalElements(), mentions);
    }
}
