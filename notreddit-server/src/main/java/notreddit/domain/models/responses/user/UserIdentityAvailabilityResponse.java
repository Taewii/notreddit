package notreddit.domain.models.responses.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserIdentityAvailabilityResponse {

    private Boolean available;
}
