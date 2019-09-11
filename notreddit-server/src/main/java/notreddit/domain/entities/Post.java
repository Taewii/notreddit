package notreddit.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post extends BaseUUIDEntity {

    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    private String title;
    private String content;
    private String imageUrl;
    private Integer upVotes;
    private Integer downVotes;
    private LocalDateTime createdOn;
    private List<Comment> comments = new ArrayList<>();
}
