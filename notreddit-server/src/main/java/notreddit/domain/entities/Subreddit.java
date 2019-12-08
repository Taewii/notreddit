package notreddit.domain.entities;

import lombok.Getter;
import lombok.Setter;
import notreddit.constants.ErrorMessages;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "subreddits")
public class Subreddit extends BaseLongEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User creator;

    @NotBlank
    @Length(min = 3, message = ErrorMessages.TITLE_LENGTH_VIOLATION_MESSAGE)
    @Column(nullable = false, unique = true)
    private String title;

    @OneToMany(mappedBy = "subreddit", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts = new HashSet<>();

    @ManyToMany(mappedBy = "subscriptions")
    private Set<User> subscribers = new HashSet<>();
}
