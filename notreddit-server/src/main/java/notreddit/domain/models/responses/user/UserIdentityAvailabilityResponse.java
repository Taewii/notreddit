package notreddit.domain.models.responses.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserIdentityAvailabilityResponse {

    private Boolean available;
}
