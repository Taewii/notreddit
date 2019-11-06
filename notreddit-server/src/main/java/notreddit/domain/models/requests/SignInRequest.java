package notreddit.domain.models.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SignInRequest {

    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;
}
