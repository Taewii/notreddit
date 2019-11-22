package notreddit.domain.models.requests;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

import static notreddit.constants.ErrorMessages.BLANK_TITLE;
import static notreddit.constants.ErrorMessages.TITLE_LENGTH_VIOLATION_MESSAGE;

@Getter
@Setter
public class SubredditCreateRequest {

    @NotBlank(message = BLANK_TITLE)
    @Length(min = 3, message = TITLE_LENGTH_VIOLATION_MESSAGE)
    private String title;
}
