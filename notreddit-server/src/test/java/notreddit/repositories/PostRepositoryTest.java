package notreddit.repositories;

import notreddit.PostgreSQLContainerInitializer;
import notreddit.domain.entities.Post;
import notreddit.domain.entities.Subreddit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SubredditRepository subredditRepository;

    private void assertPostsPageable(Page<Post> page) {
        List<Post> posts = page.getContent();
        assertEquals(5, posts.size());

        List<Post> sortedPosts = new ArrayList<>(posts);
        sortedPosts.sort((a, b) -> b.getCreatedOn().compareTo(a.getCreatedOn()));
        for (int i = 0; i < posts.size(); i++) {
            assertEquals(posts.get(i), sortedPosts.get(i));
        }
    }

    @Test
    void findAllPageable() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdOn"));

        Page<Post> page = postRepository.findAllPageable(pageable);
        assertEquals(15, page.getTotalElements());
        assertEquals(3, page.getTotalPages());

        assertPostsPageable(page);
    }

    @Test
    void findAllByUsername() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdOn"));

        Page<Post> page = postRepository.findAllByUsername("root", pageable);
        assertEquals(12, page.getTotalElements());
        assertEquals(3, page.getTotalPages());

        assertPostsPageable(page);
    }

    @Test
    void getPostsFromIdList() {
        List<UUID> postIds =
                Stream.of("965c0aa7-0fec-421c-a11e-57bd4266465f",
                        "21c8152f-61cc-4b88-a48d-0771e1396abb",
                        "d007bf28-190b-4123-ae10-69b8e8bd226f")
                        .map(UUID::fromString)
                        .collect(Collectors.toList());

        List<Post> posts = postRepository
                .getPostsFromIdList(postIds, Sort.by(Sort.Direction.DESC, "createdOn"));

        List<Post> sortedPosts = new ArrayList<>(posts);
        sortedPosts.sort((a, b) -> b.getCreatedOn().compareTo(a.getCreatedOn()));

        assertEquals(3, posts.size());
        for (int i = 0; i < posts.size(); i++) {
            assertEquals(posts.get(i), sortedPosts.get(i));
        }
    }

    @Test
    void getPostIdsBySubredditTitle() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdOn"));

        Page<UUID> page = postRepository.getPostIdsBySubredditTitle("aww", pageable);
        assertEquals(8, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void getSubscribedPostsIds() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "title"));
        Set<Subreddit> subreddits = subredditRepository.findByTitleIn(Arrays.asList("aww", "eli5", "bjj"));
        Page<UUID> posts = postRepository.getSubscribedPostsIds(subreddits, pageable);

        assertEquals(9, posts.getTotalElements());
        assertEquals(2, posts.getTotalPages());
    }

    @Test
    void findPostIdsByUserAndVoteChoice() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "title"));
        UUID userId = UUID.fromString("0cd5ebf9-1023-4164-81ad-e09e92f9cff2");

        Page<String> postIds = postRepository.findPostIdsByUserAndVoteChoice(userId, (byte) -1, pageable);

        assertEquals(2, postIds.getTotalElements());
        assertEquals(1, postIds.getTotalPages());
    }

    @Test
    void findByIdEager() {
        UUID postId = UUID.fromString("d92e1999-fd40-4ed8-b72a-faa16b54da4f");
        Optional<Post> post = postRepository.findByIdEager(postId);

        assertTrue(post.isPresent());
        assertNotNull(post.get().getSubreddit());
        assertNotNull(post.get().getComments());
        assertNotNull(post.get().getCreator());
    }

    @Test
    void findByIdWithCreatorAndComments() {
        UUID postId = UUID.fromString("d92e1999-fd40-4ed8-b72a-faa16b54da4f");
        Optional<Post> post = postRepository.findByIdWithCreatorAndComments(postId);

        assertTrue(post.isPresent());
        assertNotNull(post.get().getComments());
        assertNotNull(post.get().getCreator());
    }

    @Test
    void findByIdWithFileAnSubreddit() {
        UUID postId = UUID.fromString("d92e1999-fd40-4ed8-b72a-faa16b54da4f");
        Optional<Post> post = postRepository.findByIdWithFileAnSubreddit(postId);

        assertTrue(post.isPresent());
        assertNotNull(post.get().getSubreddit());
        assertNotNull(post.get().getCreator());
        assertNotNull(post.get().getFile());
    }
}