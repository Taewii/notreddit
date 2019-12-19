package notreddit.data.models.requests;

import lombok.Getter;
import lombok.Setter;
import notreddit.constants.ErrorMessages;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class ChangeRoleRequest {

    @NotNull(message = ErrorMessages.BLANK_USER_ID)
    private UUID userId;

    @NotBlank(message = ErrorMessages.BLANK_CURRENT_ROLE)
    private String currentRole;

    @NotBlank(message = ErrorMessages.BLANK_NEW_ROLE)
    private String newRole;
}
