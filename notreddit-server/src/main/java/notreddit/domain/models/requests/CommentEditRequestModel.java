package notreddit.domain.models.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class CommentEditRequestModel {

    @NotNull
    private UUID commentId;

    @NotBlank
    private String content;
}
