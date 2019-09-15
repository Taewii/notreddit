package notreddit.domain.models.requests;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SubredditCreateRequest {

    @NotBlank
    @Length(min = 4)
    private String title;
}
