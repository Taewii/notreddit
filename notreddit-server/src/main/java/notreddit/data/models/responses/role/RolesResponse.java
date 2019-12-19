package notreddit.data.models.responses.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RolesResponse {

    private List<String> roles;
}
