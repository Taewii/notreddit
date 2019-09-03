package armory.domain.models.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserSummaryResponse {

    private String id;
    private String username;
    private List<String> roles = new ArrayList<>();
}
