package notreddit.services.implementations;

import lombok.RequiredArgsConstructor;
import notreddit.domain.entities.Subreddit;
import notreddit.domain.entities.User;
import notreddit.domain.models.requests.SubredditCreateRequest;
import notreddit.domain.models.responses.api.ApiResponse;
import notreddit.domain.models.responses.subreddit.SubredditWithPostsAndSubscribersCountResponse;
import notreddit.repositories.SubredditRepository;
import notreddit.repositories.UserRepository;
import notreddit.services.SubredditService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static notreddit.constants.ApiResponseMessages.*;

@Service
@RequiredArgsConstructor
public class SubredditServiceImpl implements SubredditService {

    private static final String SUBREDDIT_NAMES_CACHE = "subredditNames";
    private static final String SUBREDDITS_WITH_POST_AND_SUBSCRIBER_COUNT_CACHE = "subredditWithPostAndSubscriberCount";
    private static final String SUBSCRIBED_POSTS_CACHE = "subscribedPosts";

    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public Boolean existsByTitle(String title) {
        return subredditRepository.existsByTitleIgnoreCase(title);
    }

    @Override
    public Boolean isUserSubscribedToSubreddit(String subreddit, User user) {
        return subredditRepository.isUserSubscribedToSubreddit(subreddit, user.getId());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = SUBREDDIT_NAMES_CACHE, allEntries = true),
            @CacheEvict(value = SUBREDDITS_WITH_POST_AND_SUBSCRIBER_COUNT_CACHE, allEntries = true)
    })
    public ResponseEntity<?> create(SubredditCreateRequest request, User creator) {
        if (existsByTitle(request.getTitle())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, SUBREDDIT_ALREADY_EXISTS));
        }

        Subreddit subreddit = mapper.map(request, Subreddit.class);
        subreddit.setCreator(creator);
        subredditRepository.saveAndFlush(subreddit);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/subreddit/create")
                .buildAndExpand().toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true, SUCCESSFUL_SUBREDDIT_CREATION));
    }

    @Override
    @CacheEvict(value = SUBSCRIBED_POSTS_CACHE, allEntries = true)
    public ResponseEntity<?> subscribe(String subredditTitle, User user) {
        Subreddit subreddit = subredditRepository.findByTitleIgnoreCase(subredditTitle).orElse(null);
        user = userRepository.getWithSubscriptions(user);

        if (subreddit == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, NONEXISTENT_SUBREDDIT));
        }

        user.subscribe(subreddit);
        userRepository.saveAndFlush(user);

        String responseMessage = String.format(SUCCESSFUL_SUBREDDIT_SUBSCRIPTION, subreddit.getTitle());
        return ResponseEntity.ok(new ApiResponse(true, responseMessage));
    }

    @Override
    @CacheEvict(value = SUBSCRIBED_POSTS_CACHE, allEntries = true)
    public ResponseEntity<?> unsubscribe(String subredditTitle, User user) {
        Subreddit subreddit = subredditRepository.findByTitleIgnoreCase(subredditTitle).orElse(null);
        user = userRepository.getWithSubscriptions(user);

        if (subreddit == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, NONEXISTENT_SUBREDDIT));
        }

        user.unsubscribe(subreddit);
        userRepository.saveAndFlush(user);

        String responseMessage = String.format(SUCCESSFUL_SUBREDDIT_UNSUBSCRIPTION, subreddit.getTitle());
        return ResponseEntity.ok(new ApiResponse(true, responseMessage));
    }

    @Override
    @Cacheable(SUBREDDIT_NAMES_CACHE)
    public List<String> getAllAsStrings() {
        return subredditRepository
                .findAll()
                .stream()
                .map(subreddit -> mapper.map(subreddit, String.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = SUBREDDITS_WITH_POST_AND_SUBSCRIBER_COUNT_CACHE, sync = true)
    @Override
    public List<SubredditWithPostsAndSubscribersCountResponse> getAllWithPostCount() {
        return subredditRepository.findAllWithPostAndSubscriberCount();
    }

    @Override
    public Set<String> getUserSubscriptions(User user) {
        user = userRepository.getWithSubscriptions(user);
        return user.getSubscriptions()
                .parallelStream()
                .map(Subreddit::getTitle)
                .collect(Collectors.toSet());
    }
}
