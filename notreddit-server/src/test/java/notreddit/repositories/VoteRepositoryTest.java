package notreddit.repositories;

import notreddit.PostgreSQLContainerInitializer;
import notreddit.domain.entities.User;
import notreddit.domain.entities.Vote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
class VoteRepositoryTest {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByPostIdAndUserId_withExistingVote_returnsCorrectVote() {
        UUID postId = UUID.fromString("d92e1999-fd40-4ed8-b72a-faa16b54da4f");
        UUID userId = UUID.fromString("0cd5ebf9-1023-4164-81ad-e09e92f9cff2");
        Optional<Vote> vote = voteRepository.findByPostIdAndUserId(postId, userId);

        assertTrue(vote.isPresent());
        assertEquals(1, vote.get().getChoice());
    }

    @Test
    void findByPostIdAndUserId_withNonExistentVote_returnsEmptyOptional() {
        UUID postId = UUID.fromString("c1713ccd-97e8-4b50-9d6e-aa0fa2022787");
        UUID userId = UUID.fromString("0cd5ebf9-1023-4164-81ad-e09e92f9cff2");
        Optional<Vote> vote = voteRepository.findByPostIdAndUserId(postId, userId);

        assertFalse(vote.isPresent());
    }

    @Test
    void findByCommentIdAndUserId_withExistingVote_returnsCorrectVote() {
        UUID commentId = UUID.fromString("6ba627e8-7a3f-4e9b-ae44-675ccdbc4cf2");
        UUID userId = UUID.fromString("0cd5ebf9-1023-4164-81ad-e09e92f9cff2");
        Optional<Vote> vote = voteRepository.findByCommentIdAndUserId(commentId, userId);

        assertTrue(vote.isPresent());
        assertEquals(-1, vote.get().getChoice());
    }

    @Test
    void findByCommentIdAndUserId_withNonExistentVote_returnsEmptyOptional() {
        UUID commentId = UUID.fromString("ff3b4efc-34b0-4dc9-91ab-8c99ac5140cc");
        UUID userId = UUID.fromString("0cd5ebf9-1023-4164-81ad-e09e92f9cff2");
        Optional<Vote> vote = voteRepository.findByCommentIdAndUserId(commentId, userId);

        assertFalse(vote.isPresent());
    }

    @Test
    void findPostVotesByUser_shouldReturnCorrectData() {
        UUID userId = UUID.fromString("0cd5ebf9-1023-4164-81ad-e09e92f9cff2");
        User user = userRepository.findById(userId).orElseThrow();

        List<Vote> votes = voteRepository.findPostVotesByUser(user);
        assertEquals(7, votes.size());
        votes.forEach(v -> assertEquals("root", v.getUser().getUsername()));
    }

    @Test
    void findCommentVotesByUser_shouldReturnCorrectData() {
        UUID userId = UUID.fromString("0cd5ebf9-1023-4164-81ad-e09e92f9cff2");
        User user = userRepository.findById(userId).orElseThrow();

        List<Vote> votes = voteRepository.findCommentVotesByUser(user);
        assertEquals(9, votes.size());
        votes.forEach(v -> assertEquals("root", v.getUser().getUsername()));
    }
}