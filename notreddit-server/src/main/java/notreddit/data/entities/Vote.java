package notreddit.data.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "votes")
public class Vote extends BaseUUIDEntity {

    @Min(-1)
    @Max(1)
    @Column(nullable = false)
    private byte choice;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Comment.class)
    private Votable comment;

    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE,
            targetEntity = Post.class)
    private Votable post;

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime createdOn;
}
