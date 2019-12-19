package notreddit.repositories;

import notreddit.PostgreSQLContainerInitializer;
import notreddit.data.entities.Subreddit;
import notreddit.data.entities.User;
import notreddit.data.models.responses.subreddit.SubredditWithPostsAndSubscribersCountResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

import static org.junit.Assert.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
class SubredditRepositoryTest {

    @Autowired
    private SubredditRepository subredditRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByTitleIgnoreCase_withExistingSubreddit_returnsTrue() {
        Boolean result = subredditRepository.existsByTitleIgnoreCase("eli5");
        assertTrue(result);
    }

    @Test
    void existsByTitleIgnoreCase_withNonExistentSubreddit_returnsFalse() {
        Boolean result = subredditRepository.existsByTitleIgnoreCase("nonexistent");
        assertFalse(result);
    }

    @Test
    void findByTitleIgnoreCase_withExistingSubreddit_shouldSaidSubreddit() {
        Optional<Subreddit> subreddit = subredditRepository.findByTitleIgnoreCase("aww");

        assertTrue(subreddit.isPresent());
        assertEquals("aww", subreddit.get().getTitle());
    }

    @Test
    void findByTitleIgnoreCase_withNonExistentSubreddit_returnEmptyOptional() {
        Optional<Subreddit> subreddit = subredditRepository.findByTitleIgnoreCase("nonexistent");
        assertFalse(subreddit.isPresent());
    }

    @Test
    void findAllWithPostCount_shouldReturnCorrectData() {
        List<SubredditWithPostsAndSubscribersCountResponse> responses =
                subredditRepository.findAllWithPostAndSubscriberCount();

        assertEquals(15, responses.size());
        assertEquals(SubredditWithPostsAndSubscribersCountResponse.class, responses.get(0).getClass());
        assertNotNull(responses.get(0).getPostCount());
        assertNotNull(responses.get(0).getSubscriberCount());
        assertNotNull(responses.get(0).getTitle());
    }

    @Test
    void findByTitleIn_with2Items_returnsTheWantedSubreddits() {
        Set<Subreddit> subreddits = subredditRepository.findByTitleIn(Arrays.asList("aww", "eli5"));
        assertEquals(2, subreddits.size());
    }

    @Test
    void findByTitleIn_with1Item_returnsTheWantedSubreddit() {
        Set<Subreddit> subreddits = subredditRepository.findByTitleIn(Collections.singletonList("aww"));
        assertEquals(1, subreddits.size());
        subreddits.forEach(s -> assertEquals("aww", s.getTitle()));
    }

    @Test
    void isUserSubscribedToSubreddit_withSubscribedUser_returnsTrue() {
        User user = userRepository.findByUsernameOrEmailIgnoreCase("user", "nonexistent@email.bg")
                .orElseThrow(NoSuchElementException::new);
        Boolean result = subredditRepository.isUserSubscribedToSubreddit("aww", user.getId());
        assertTrue(result);
    }

    @Test
    void isUserSubscribedToSubreddit_withNotSubscribedUser_returnsFalse() {
        User user = userRepository.findByUsernameOrEmailIgnoreCase("user", "nonexistent@email.bg")
                .orElseThrow(NoSuchElementException::new);
        Boolean result = subredditRepository.isUserSubscribedToSubreddit("eli5", user.getId());
        assertFalse(result);
    }
}