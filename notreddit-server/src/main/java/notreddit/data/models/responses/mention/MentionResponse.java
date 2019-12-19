package notreddit.data.models.responses.mention;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentionResponse {

    private long total;
    private List<MentionResponseModel> mentions;
}
