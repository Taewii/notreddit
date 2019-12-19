package notreddit.data.models.responses.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UsersResponse {

    private List<UserSummaryResponse> users;
}
