package notreddit.data.entities;

import java.util.UUID;

public interface Votable {

    void upvote();

    void downvote();

    int getDownvotes();

    void setDownvotes(int downvotes);

    int getUpvotes();

    void setUpvotes(int upvotes);

    UUID getId();
}
