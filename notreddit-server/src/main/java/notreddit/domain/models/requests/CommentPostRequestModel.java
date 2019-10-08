package notreddit.domain.models.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class CommentPostRequestModel {

    @NotNull
    private UUID postId;

    @NotBlank
    private String content;

    private UUID parentId;
}
