package notreddit.domain.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubredditAvailabilityResponse {

    private Boolean available;
}
