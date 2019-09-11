package notreddit.domain.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "subreddits")
public class Subreddit extends BaseLongEntity {

    private String title;
    private Set<Post> posts = new HashSet<>();
}
