package notreddit.repositories;

import notreddit.domain.entities.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Long> {

    Boolean existsByTitle(String title);
}
