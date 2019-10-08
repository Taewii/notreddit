package notreddit.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment extends BaseUUIDEntity {

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Post post;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "NUMERIC DEFAULT 0")
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parent;

    @NotNull
    @PastOrPresent
    private LocalDateTime createdAt;
}
