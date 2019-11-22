package notreddit.domain.models.requests;

import lombok.Getter;
import lombok.Setter;
import notreddit.domain.validations.annotations.MatchingFieldsConstraint;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import static notreddit.constants.ErrorMessages.*;

@Getter
@Setter
@MatchingFieldsConstraint(fields = {"password", "confirmPassword"})
public class SignUpRequest {

    @NotBlank(message = BLANK_USERNAME)
    @Length(min = 4, message = USERNAME_LENGTH_VIOLATION_MESSAGE)
    private String username;

    @NotBlank(message = BLANK_PASSWORD)
    @Length(min = 6, message = PASSWORD_LENGTH_VIOLATION_MESSAGE)
    private String password;

    @NotBlank(message = BLANK_CONFIRM_PASSWORD)
    @Length(min = 6, message = CONFIRM_PASSWORD_LENGTH_VIOLATION_MESSAGE)
    private String confirmPassword;

    @Email(message = INVALID_EMAIL)
    @NotBlank(message = BLANK_EMAIL)
    private String email;
}
