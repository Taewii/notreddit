package notreddit.domain.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UsersResponse {

    private List<UserSummaryResponse> users;
}
