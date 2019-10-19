package notreddit.domain.models.responses.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostVoteUserChoiceResponse {

    private boolean hasVoted;
    private Byte choice;
}
