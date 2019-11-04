package notreddit.domain.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "subreddits")
public class Subreddit extends BaseLongEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User creator;

    @NotBlank
    @Length(min = 3)
    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "subreddit", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts = new HashSet<>();
}
