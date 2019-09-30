package notreddit.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "votes")
public class Vote extends BaseUUIDEntity {

    @NotNull
    @Column(nullable = false)
    private byte choice;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
