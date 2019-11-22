package notreddit.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final public class ApiResponseMessages {

    public static final String NONEXISTENT_COMMENT_OR_NOT_CREATOR = "Comment doesn't exist, or you are not the creator.";
    public static final String SUCCESSFUL_COMMENT_CREATION = "Comment created successfully.";
    public static final String SUCCESSFUL_COMMENT_DELETION = "Comment deleted successfully.";
    public static final String SUCCESSFUL_COMMENT_EDITING = "Comment edited successfully.";
    public static final String NONEXISTENT_COMMENT = "Comment doesn't exist.";
    public static final String NONEXISTENT_POST_OR_COMMENT = "Post/Comment does't exist.";

    public static final String SUCCESSFUL_POST_CREATION = "Post created successfully.";
    public static final String SUCCESSFUL_POST_DELETION = "Post deleted successfully.";
    public static final String NONEXISTENT_POST = "Post doesn't exist.";
    public static final String ONLY_ONE_UPLOADED_METHOD_ALLOWED = "You can't have both url and uploaded image.";
    public static final String FILE_SIZE_OVER_10MB = "File size is over the limit of 10MB.";

    public static final String SUCCESSFUL_SUBREDDIT_CREATION = "Subreddit created successfully.";
    public static final String SUBREDDIT_ALREADY_EXISTS = "Subreddit name already exists.";
    public static final String NONEXISTENT_SUBREDDIT = "Subreddit doesn't exist.";
    public static final String SUCCESSFUL_SUBREDDIT_SUBSCRIPTION = "Successfully subscribed to %s.";
    public static final String SUCCESSFUL_SUBREDDIT_UNSUBSCRIPTION = "Successfully unsubscribed from %s.";

    public static final String NONEXISTENT_MENTION_OR_NOT_RECEIVER = "No such mention, or you are not the receiver of the mention.";
    public static final String MENTION_MARKED_AS = "Mention marked as %s.";

    public static final String SUCCESSFUL_VOTE_DELETION = "Vote deselected successfully.";
    public static final String SUCCESSFUL_VOTE_REGISTRATION = "Vote registered successfully.";

    public static final String NONEXISTENT_USERNAME_OR_EMAIL = "User with such username or email doesn't exist: ";
    public static final String NONEXISTENT_USER_WITH_ID = "User with id: %s doesn't exist.";
    public static final String USERNAME_IS_TAKEN = "Username is already taken!";
    public static final String EMAIL_IS_TAKEN = "Email Address already in use!";
    public static final String SUCCESSFUL_USER_REGISTRATION = "User registered successfully.";
    public static final String SUCCESSFUL_USER_DELETION = "User %s deleted successfully.";
    public static final String CANNOT_CHANGE_ROLE_TO_FROM_ROOT = "Cannot change role form/to ROOT.";
    public static final String CHANGED_USERS_ROLE_TO = "Changed %s's role to %s.";

}
