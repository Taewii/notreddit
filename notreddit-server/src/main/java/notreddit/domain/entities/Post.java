package notreddit.domain.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post extends BaseUUIDEntity implements Votable {

    @NotNull
    @JoinColumn(name = "creator_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User creator;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Subreddit subreddit;

    @Length(min = 4)
    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "NUMERIC DEFAULT 0")
    private int upvotes;

    @Column(nullable = false, columnDefinition = "NUMERIC DEFAULT 0")
    private int downvotes;

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime createdOn;

    @OneToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "post")
    private File file;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Override
    public void upvote() {
        this.setUpvotes(this.getUpvotes() + 1);
    }

    @Override
    public void downvote() {
        this.setDownvotes(this.getDownvotes() + 1);
    }

    public void addFile(File file) {
        this.setFile(file);
        file.setPost(this);
    }
}
