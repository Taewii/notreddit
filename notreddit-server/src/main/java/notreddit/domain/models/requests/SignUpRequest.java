package notreddit.domain.models.requests;

import notreddit.domain.validations.annotations.MatchingFieldsConstraint;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@MatchingFieldsConstraint(fields = {"password", "confirmPassword"})
public class SignUpRequest {

    @NotBlank
    @Length(min = 4)
    private String username;

    @NotBlank
    @Length(min = 6)
    private String password;

    @NotBlank
    @Length(min = 6)
    private String confirmPassword;

    @Email
    @NotBlank
    private String email;
}
