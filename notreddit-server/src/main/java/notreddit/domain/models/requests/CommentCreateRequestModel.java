package notreddit.domain.models.requests;

import lombok.Getter;
import lombok.Setter;
import notreddit.constants.ErrorMessages;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class CommentCreateRequestModel {

    @NotNull(message = ErrorMessages.BLANK_POST_ID)
    private UUID postId;

    @NotBlank(message = ErrorMessages.BLANK_CONTENT)
    private String content;

    private UUID parentId;
}
