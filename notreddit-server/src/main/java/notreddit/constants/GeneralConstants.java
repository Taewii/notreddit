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
    }}.stream().map(String::toLowerCase).collect(Collectors.toList());

    public static final String POSTS_BY_ID_CACHE = "byId";
    public static final String POSTS_BY_USERNAME_CACHE = "byUsername";
    public static final String POSTS_BY_SUBREDDIT_CACHE = "bySubreddit";
    public static final String SUBSCRIBED_POSTS_CACHE = "subscribedPosts";

    public static final String COMMENTS_BY_POST_CACHE = "allByPost";
    public static final String COMMENTS_BY_USERNAME = "allByUsername";
}
