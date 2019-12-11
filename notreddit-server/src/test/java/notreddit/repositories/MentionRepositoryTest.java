package notreddit.repositories;

import notreddit.PostgreSQLContainerInitializer;
import notreddit.domain.entities.Mention;
import notreddit.domain.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

import static org.junit.Assert.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
class MentionRepositoryTest {

    @Autowired
    private MentionRepository mentionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByIdWithReceiver_withExistingMention_returnsSaidMentionAndItsReceiver() {
        UUID mentionId = UUID.fromString("04256266-d201-43d6-8df2-534c2d36fd07");
        Optional<Mention> byIdWithReceiver = mentionRepository.findByIdWithReceiver(mentionId);

        assertTrue(byIdWithReceiver.isPresent());
        Mention mention = byIdWithReceiver.get();
        assertEquals("root", mention.getReceiver().getUsername());
        assertFalse(mention.isRead());
    }

    @Test
    void findByIdWithReceiver_withNonExistentMention_returnsEmptyOptional() {
        Optional<Mention> byIdWithReceiver = mentionRepository.findByIdWithReceiver(UUID.randomUUID());
        assertFalse(byIdWithReceiver.isPresent());
    }

    @Test
    void getUnreadMentionCountByUser_shouldReturnCorrectUnreadMentionCount() {
        UUID userId = UUID.fromString("0cd5ebf9-1023-4164-81ad-e09e92f9cff2");
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);

        int unreadMentions = mentionRepository.getUnreadMentionCountByUser(user);

        assertEquals(4, unreadMentions);
    }

    @Test
    void getUsersMentions_shouldReturnCorrectlyOrderedAndPagedEntities() {
        Pageable pageable = PageRequest.of(0, 5);
        UUID userId = UUID.fromString("0cd5ebf9-1023-4164-81ad-e09e92f9cff2");
        User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);

        Page<Mention> usersMentions = mentionRepository.getUsersMentions(user, pageable);

        assertEquals(8, usersMentions.getTotalElements());
        assertEquals(2, usersMentions.getTotalPages());

        List<Mention> mentions = usersMentions.getContent();
        assertEquals(5, mentions.size());

        List<Mention> sortedMention = new ArrayList<>(mentions);
        sortedMention.sort((a, b) -> Boolean.compare(a.isRead(), b.isRead()));

        for (int i = 0; i < mentions.size(); i++) {
            assertEquals("root", mentions.get(i).getReceiver().getUsername());
            assertEquals(mentions.get(i), sortedMention.get(i));
        }
    }
}