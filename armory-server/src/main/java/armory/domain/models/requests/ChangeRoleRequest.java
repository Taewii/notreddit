package armory.domain.models.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class ChangeRoleRequest {

    @NotNull
    private UUID userId;

    @NotBlank
    private String currentRole;

    @NotBlank
    private String newRole;
}
