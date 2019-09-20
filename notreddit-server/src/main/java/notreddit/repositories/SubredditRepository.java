package notreddit.repositories;

import notreddit.domain.entities.Subreddit;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Long> {

    Boolean existsByTitle(String title);

    Optional<Subreddit> findByTitleIgnoreCase(@Length(min = 4) @NotBlank String title);
}
