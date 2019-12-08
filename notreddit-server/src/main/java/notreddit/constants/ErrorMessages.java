package notreddit.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final public class ErrorMessages {

    public static final String ACCESS_FORBIDDEN = "You don't have the authority to do that.";
    public static final String TOO_MANY_REQUESTS = "Too many requests.";
    public static final String FIELDS_ARE_NOT_MATCHING = "Fields are not matching.";

    public static final String BLANK_TITLE = "Title must not be blank.";
    public static final String BLANK_USERNAME = "Username must not be blank.";
    public static final String BLANK_PASSWORD = "Password must not be blank.";
    public static final String BLANK_CONFIRM_PASSWORD = "Confirm password must not be blank.";
    public static final String BLANK_EMAIL = "Email must not be blank.";
    public static final String BLANK_SUBREDDIT = "Subreddit name must not be blank.";
    public static final String BLANK_CONTENT = "Comment must not be blank.";
    public static final String BLANK_CURRENT_ROLE = "Current role must not be blank.";
    public static final String BLANK_USER_ID = "User ID must not be blank.";
    public static final String BLANK_POST_ID = "Post ID must not be blank.";
    public static final String BLANK_COMMENT_ID = "Comment ID must not be blank.";
    public static final String BLANK_NEW_ROLE = "The new role must not be blank.";
    public static final String INVALID_EMAIL = "The given email is not valid.";

    public static final String TITLE_LENGTH_VIOLATION_MESSAGE = "Title length must be more or equal to {min}.";
    public static final String USERNAME_LENGTH_VIOLATION_MESSAGE = "Username length must be more or equal to {min}.";
    public static final String PASSWORD_LENGTH_VIOLATION_MESSAGE = "Password length must be more or equal to {min}.";
    public static final String CONFIRM_PASSWORD_LENGTH_VIOLATION_MESSAGE = "Confirm password length must be more or equal to {min}.";
}
