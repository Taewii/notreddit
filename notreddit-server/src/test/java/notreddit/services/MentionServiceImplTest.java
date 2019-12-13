package notreddit.services;

import notreddit.SingletonModelMapper;
import notreddit.domain.entities.Comment;
import notreddit.domain.entities.Mention;
import notreddit.domain.entities.Post;
import notreddit.domain.entities.User;
import notreddit.domain.models.responses.mention.MentionResponse;
import notreddit.domain.models.responses.mention.MentionResponseModel;
import notreddit.repositories.MentionRepository;
import notreddit.services.implementations.MentionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MentionServiceImplTest {

    private MentionService mentionService;
    private MentionRepository mentionRepository;

    @BeforeEach
    public void setUp() {
        mentionRepository = mock(MentionRepository.class);
        mentionService = new MentionServiceImpl(mentionRepository, SingletonModelMapper.mapper());
    }

    private Page<Mention> createMentions(int count, Pageable pageable) {
        List<Mention> mentions = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setUsername("username" + i);
            Post post = new Post();
            post.setId(UUID.randomUUID());
            post.setTitle("title" + i);
            Comment comment = new Comment();
            comment.setContent("content");
            comment.setPost(post);

            Mention mention = new Mention();
            mention.setId(UUID.randomUUID());
            mention.setRead(i % 2 == 0);
            mention.setCreator(user);
            mention.setComment(comment);
            mention.setCreatedOn(LocalDateTime.now());
            mentions.add(mention);
        }

        return new PageImpl<>(mentions, pageable, mentions.size());
    }

    @Test
    void getUnreadMentionCountByUser_shouldWorkCorrectly() {
        when(mentionRepository.getUnreadMentionCountByUser(any(User.class))).thenReturn(5);
        int actual = mentionService.getUnreadMentionCountByUser(mock(User.class));
        assertEquals(5, actual);
    }

    @Test
    void getMentionByUser_shouldWorkCorrectly() {
        Pageable pageable = PageRequest.of(0, 3);
        when(mentionRepository.getUsersMentions(any(User.class), eq(pageable)))
                .thenReturn(createMentions(3, pageable));

        MentionResponse result = mentionService.getMentionByUser(new User(), pageable);

        assertEquals(3, result.getMentions().size());
        assertEquals(3, result.getTotal());
        MentionResponseModel firstMention = result.getMentions().get(0);
        assertEquals(MentionResponseModel.class, firstMention.getClass());
        assertNotNull(firstMention.getCommentContent());
        assertNotNull(firstMention.getCommentPostId());
        assertNotNull(firstMention.getCommentPostTitle());
        assertNotNull(firstMention.getCreatorUsername());
    }

    @Test
    void getMentionByUser_withNoData_returnEmptyResponse() {
        Pageable pageable = PageRequest.of(0, 3);
        when(mentionRepository.getUsersMentions(any(User.class), eq(pageable)))
                .thenReturn(createMentions(0, pageable));

        MentionResponse result = mentionService.getMentionByUser(new User(), pageable);

        assertTrue(result.getMentions().isEmpty());
        assertEquals(0, result.getTotal());
    }

    @Test
    void mark_withValidReadData_shouldCorrectlyMarkReadAndSave() {
        Mention mention = mock(Mention.class);
        User user = mock(User.class);

        when(mentionRepository.findByIdWithReceiver(any(UUID.class))).thenReturn(Optional.of(mention));
        when(mention.getReceiver()).thenReturn(user);
        when(user.getUsername()).thenReturn("username");

        mentionService.mark(true, user, UUID.randomUUID());

        verify(mention).setRead(true);
        verify(mentionRepository).saveAndFlush(mention);
    }

    @Test
    void mark_withValidUnreadData_shouldCorrectlyMarkUnreadAndSave() {
        Mention mention = mock(Mention.class);
        User user = mock(User.class);

        when(mentionRepository.findByIdWithReceiver(any(UUID.class))).thenReturn(Optional.of(mention));
        when(mention.getReceiver()).thenReturn(user);
        when(user.getUsername()).thenReturn("username");

        mentionService.mark(false, user, UUID.randomUUID());

        verify(mention).setRead(false);
        verify(mentionRepository).saveAndFlush(mention);
    }

    @Test
    void mark_withNonExistingMention_shouldNotAttemptToSave() {
        when(mentionRepository.findByIdWithReceiver(any(UUID.class))).thenReturn(Optional.empty());
        mentionService.mark(false, null, UUID.randomUUID());

        verify(mentionRepository, never()).saveAndFlush(any());
    }

    @Test
    void mark_withUserNotBeingTheReceiver_shouldNotAttemptToSave() {
        Mention mention = mock(Mention.class);
        User user = mock(User.class);
        User receiver = mock(User.class);

        when(mentionRepository.findByIdWithReceiver(any(UUID.class))).thenReturn(Optional.of(mention));
        when(mention.getReceiver()).thenReturn(receiver);
        when(receiver.getUsername()).thenReturn("random username");
        when(user.getUsername()).thenReturn("username");

        mentionService.mark(false, user, UUID.randomUUID());

        verify(mention, never()).setRead(anyBoolean());
        verify(mentionRepository, never()).saveAndFlush(any());
    }
}