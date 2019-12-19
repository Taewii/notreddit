package notreddit.data.models.requests;

import lombok.Getter;
import lombok.Setter;
import notreddit.constants.ErrorMessages;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class CommentEditRequest {

    @NotNull(message = ErrorMessages.BLANK_COMMENT_ID)
    private UUID commentId;

    @NotBlank(message = ErrorMessages.BLANK_CONTENT)
    private String content;
}
