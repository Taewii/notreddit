package notreddit.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final public class GeneralConstants {

    public static final List<String> DEFAULT_SUBREDDITS = new ArrayList<String>() {{
        add("aww");
        add("HumansBeingBros");
        add("EyeBleach");
    }}.stream().map(String::toLowerCase).collect(Collectors.toUnmodifiableList());
}
