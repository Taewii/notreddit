package notreddit.repositories;

import notreddit.domain.entities.Subreddit;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Long> {

    Boolean existsByTitle(String title);
}
