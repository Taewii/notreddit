package notreddit.services;

import notreddit.SingletonModelMapper;
import notreddit.domain.entities.Subreddit;
import notreddit.domain.entities.User;
import notreddit.domain.models.requests.SubredditCreateRequest;
import notreddit.domain.models.responses.subreddit.SubredditWithPostsAndSubscribersCountResponse;
import notreddit.repositories.SubredditRepository;
import notreddit.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubredditServiceImplTest {

    private SubredditService subredditService;
    private UserRepository userRepository;
    private SubredditRepository subredditRepository;

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        subredditRepository = mock(SubredditRepository.class);
        userRepository = mock(UserRepository.class);
        subredditService = new SubredditServiceImpl(
                subredditRepository,
                userRepository,
                SingletonModelMapper.mapper());
    }

    private List<Subreddit> createSubreddits(int count) {
        List<Subreddit> subreddits = new ArrayList<>();

        for (long i = 0; i < count; i++) {
            Subreddit subreddit = new Subreddit();
            subreddit.setId(i);
            subreddit.setTitle("title" + i);
            subreddits.add(subreddit);
        }

        return subreddits;
    }

    private List<SubredditWithPostsAndSubscribersCountResponse> createSubredditsWithPostCount(int count) {
        List<SubredditWithPostsAndSubscribersCountResponse> subreddits = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            SubredditWithPostsAndSubscribersCountResponse subreddit = new SubredditWithPostsAndSubscribersCountResponse();
            subreddit.setTitle("title" + i);
            subreddit.setSubscriberCount(i);
            subreddit.setPostCount(i);
            subreddits.add(subreddit);
        }

        return subreddits;
    }

    @Test
    void create_withValidData_shouldMapAndSaveEntity() {
        User user = mock(User.class);
        SubredditCreateRequest request = new SubredditCreateRequest();
        request.setTitle("title");
        when(subredditRepository.existsByTitleIgnoreCase(any(String.class))).thenReturn(false);

        subredditService.create(request, user);

        verify(subredditRepository).saveAndFlush(any(Subreddit.class));
    }

    @Test
    void create_withExistingTitle_shouldDoNothing() {
        User user = mock(User.class);
        SubredditCreateRequest request = new SubredditCreateRequest();
        request.setTitle("title");
        when(subredditRepository.existsByTitleIgnoreCase(any(String.class))).thenReturn(true);

        subredditService.create(request, user);

        verify(subredditRepository, never()).saveAndFlush(any());
    }

    @Test
    void existsByTitle_whenSubredditExists_returnsTrue() {
        when(subredditRepository.existsByTitleIgnoreCase(any(String.class))).thenReturn(true);
        Boolean result = subredditService.existsByTitle("title");
        assertTrue(result);
    }

    @Test
    void existsByTitle_whenSubredditDoesntExists_returnsFalse() {
        when(subredditRepository.existsByTitleIgnoreCase(any(String.class))).thenReturn(false);
        Boolean result = subredditService.existsByTitle("title");
        assertFalse(result);
    }

    @Test
    void isUserSubscribedToSubreddit_wheUserIsSubscribed_returnsTrue() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(subredditRepository.isUserSubscribedToSubreddit(any(String.class), any(UUID.class))).thenReturn(true);
        Boolean result = subredditService.isUserSubscribedToSubreddit("title", user);
        assertTrue(result);
    }

    @Test
    void isUserSubscribedToSubreddit_wheUserIsNotSubscribed_returnsFalse() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(subredditRepository.isUserSubscribedToSubreddit(any(String.class), any(UUID.class))).thenReturn(false);
        Boolean result = subredditService.isUserSubscribedToSubreddit("title", user);
        assertFalse(result);
    }

    @Test
    void getAllAsStrings_shouldWorkCorrectly() {
        when(subredditRepository.findAll()).thenReturn(createSubreddits(3));
        List<String> result = subredditService.getAllAsStrings();

        assertEquals(3, result.size());
        assertEquals(String.class, result.get(0).getClass());

        for (int i = 0; i < result.size(); i++) {
            assertEquals("title" + i, result.get(i));
        }
    }

    @Test
    void getAllAsStrings_withNoSubreddits_returnsEmptyList() {
        when(subredditRepository.findAll()).thenReturn(new ArrayList<>());
        List<String> result = subredditService.getAllAsStrings();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllWithPostCount_shouldWorkCorrectly() {
        when(subredditRepository.findAllWithPostAndSubscriberCount()).thenReturn(createSubredditsWithPostCount(3));
        List<SubredditWithPostsAndSubscribersCountResponse> result = subredditService.getAllWithPostCount();

        assertEquals(3, result.size());
        assertEquals(SubredditWithPostsAndSubscribersCountResponse.class, result.get(0).getClass());
        for (int i = 0; i < result.size(); i++) {
            assertEquals("title" + i, result.get(i).getTitle());
            assertEquals((Integer) i, result.get(i).getPostCount());
            assertEquals((Integer) i, result.get(i).getSubscriberCount());
        }
    }

    @Test
    void getAllWithPostCount_withNoSubreddits_returnsEmptyList() {
        when(subredditRepository.findAllWithPostAndSubscriberCount()).thenReturn(new ArrayList<>());
        List<SubredditWithPostsAndSubscribersCountResponse> result = subredditService.getAllWithPostCount();

        assertTrue(result.isEmpty());
    }

    @Test
    void subscribe_withValidData_shouldSubscribeAndSaveUserEntity() {
        User user = mock(User.class);
        when(userRepository.getWithSubscriptions(any(User.class))).thenReturn(user);
        Subreddit subreddit = mock(Subreddit.class);
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(subreddit));

        subredditService.subscribe("subreddit", user);

        verify(user).subscribe(subreddit);
        verify(userRepository).saveAndFlush(user);
    }

    @Test
    void subscribe_withInvalidData_shouldNotDoAnything() {
        User user = mock(User.class);
        when(userRepository.getWithSubscriptions(any(User.class))).thenReturn(user);
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.empty());

        subredditService.subscribe("subreddit", user);

        verify(user, never()).subscribe(any(Subreddit.class));
        verify(userRepository, never()).saveAndFlush(user);
    }

    @Test
    void unsubscribe_withValidData_shouldSubscribeAndSaveUserEntity() {
        User user = mock(User.class);
        when(userRepository.getWithSubscriptions(any(User.class))).thenReturn(user);
        Subreddit subreddit = mock(Subreddit.class);
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(subreddit));

        subredditService.unsubscribe("subreddit", user);

        verify(user).unsubscribe(subreddit);
        verify(userRepository).saveAndFlush(user);
    }

    @Test
    void unsubscribe_withInvalidData_shouldNotDoAnything() {
        User user = mock(User.class);
        when(userRepository.getWithSubscriptions(any(User.class))).thenReturn(user);
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.empty());

        subredditService.unsubscribe("subreddit", user);

        verify(user, never()).subscribe(any(Subreddit.class));
        verify(userRepository, never()).saveAndFlush(user);
    }

    @Test
    void getUserSubscriptions_shouldWorkCorrectly() {
        User user = mock(User.class);
        when(user.getSubscriptions()).thenReturn(new HashSet<>(createSubreddits(3)));
        when(userRepository.getWithSubscriptions(any(User.class))).thenReturn(user);

        Set<String> result = subredditService.getUserSubscriptions(user);

        assertEquals(3, result.size());
        assertTrue(result.contains("title0"));
        assertTrue(result.contains("title1"));
        assertTrue(result.contains("title2"));
    }

    @Test
    void getUserSubscriptions_withNoSubreddits_returnsEmptySet() {
        User user = mock(User.class);
        when(user.getSubscriptions()).thenReturn(new HashSet<>());
        when(userRepository.getWithSubscriptions(any(User.class))).thenReturn(user);

        Set<String> result = subredditService.getUserSubscriptions(user);

        assertTrue(result.isEmpty());
    }
}