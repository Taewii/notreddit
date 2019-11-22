package notreddit.services;

import notreddit.domain.entities.Subreddit;
import notreddit.domain.entities.User;
import notreddit.domain.models.requests.SubredditCreateRequest;
import notreddit.domain.models.responses.api.ApiResponse;
import notreddit.domain.models.responses.subreddit.SubredditWithPostCountResponse;
import notreddit.repositories.SubredditRepository;
import notreddit.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static notreddit.constants.ApiResponseMessages.*;

@Service
public class SubredditServiceImpl implements SubredditService {

    static final List<String> DEFAULT_SUBREDDITS = new ArrayList<String>() {{
        add("aww");
        add("HumansBeingBros");
        add("EyeBleach");
    }}.stream().map(String::toLowerCase).collect(Collectors.toUnmodifiableList());

    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Autowired
    public SubredditServiceImpl(SubredditRepository subredditRepository,
                                UserRepository userRepository,
                                ModelMapper mapper) {
        this.subredditRepository = subredditRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
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
                .fromCurrentContextPath().path("/api/subreddit/create")
                .buildAndExpand().toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true, SUCCESSFUL_SUBREDDIT_CREATION));
    }

    @Override
    public Boolean existsByTitle(String title) {
        return subredditRepository.existsByTitleIgnoreCase(title);
    }

    @Override
    public Boolean isUserSubscribedToSubreddit(String subreddit, User user) {
        return subredditRepository.isUserSubscribedToSubreddit(subreddit, user.getId());
    }

    @Override
    public List<String> getAllAsStrings() {
        return subredditRepository
                .findAll()
                .stream()
                .map(subreddit -> mapper.map(subreddit, String.class))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<SubredditWithPostCountResponse> getAllWithPostCount() {
        return subredditRepository.findAllWithPostCount();
    }

    @Override
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
    public Set<String> getUserSubscriptions(User user) {
        user = userRepository.getWithSubscriptions(user);
        return user.getSubscriptions()
                .parallelStream()
                .map(Subreddit::getTitle)
                .collect(Collectors.toUnmodifiableSet());
    }
}
