package notreddit.domain.models.responses.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import notreddit.domain.models.responses.Identifiable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserSummaryResponse extends Identifiable {

    private String username;
    private boolean enabled;
    private List<String> roles = new ArrayList<>();
}
