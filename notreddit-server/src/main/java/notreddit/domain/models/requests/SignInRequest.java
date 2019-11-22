package notreddit.domain.models.requests;

import lombok.Getter;
import lombok.Setter;
import notreddit.constants.ErrorMessages;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SignInRequest {

    @NotBlank(message = ErrorMessages.BLANK_USERNAME)
    private String usernameOrEmail;

    @NotBlank(message = ErrorMessages.BLANK_PASSWORD)
    private String password;
}
