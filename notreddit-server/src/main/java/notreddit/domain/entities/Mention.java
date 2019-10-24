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
@Table(name = "mentions")
public class Mention extends BaseUUIDEntity {

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User creator;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User receiver;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Comment comment;

    @NotNull
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isRead;

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private LocalDateTime createdOn;
}
