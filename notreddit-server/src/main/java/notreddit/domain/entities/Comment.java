package notreddit.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment extends BaseUUIDEntity {

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private User creator;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Post post;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "NUMERIC DEFAULT 0")
    private int upvotes;

    @Column(nullable = false, columnDefinition = "NUMERIC DEFAULT 0")
    private int downvotes;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<Comment> children;

    @NotNull
    @PastOrPresent
    private LocalDateTime createdOn;

    public void addChild(Comment comment) {
        this.getChildren().add(comment);
    }

    public void upvote() {
        this.setUpvotes(this.getUpvotes() + 1);
    }

    public void downvote() {
        this.setDownvotes(this.getDownvotes() + 1);
    }
}
