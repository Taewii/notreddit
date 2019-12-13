package notreddit.web.controllers;

import lombok.RequiredArgsConstructor;
import notreddit.domain.entities.User;
import notreddit.domain.models.requests.PostCreateRequest;
import notreddit.domain.models.requests.PostEditRequest;
import notreddit.domain.models.responses.post.PostDetailsResponseModel;
import notreddit.domain.models.responses.post.PostEditResponseModel;
import notreddit.domain.models.responses.post.PostsResponseModel;
import notreddit.services.PostService;
import notreddit.services.VoteService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final VoteService voteService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @ModelAttribute PostCreateRequest request,
                                    @AuthenticationPrincipal User creator) {
        return postService.create(request, creator);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/edit")
    public ResponseEntity<?> edit(@Valid @ModelAttribute PostEditRequest request,
                                  @AuthenticationPrincipal User user) {
        return postService.edit(request, user);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam UUID postId,
                                    @AuthenticationPrincipal User user) {
        return postService.delete(postId, user);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/vote")
    public ResponseEntity<?> vote(@RequestParam byte choice,
                                  @RequestParam UUID postId,
                                  @AuthenticationPrincipal User user) {
        return voteService.voteForPostOrComment(choice, postId, null, user);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public PostDetailsResponseModel findById(@PathVariable UUID id) {
        return postService.findById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{id}")
    public PostEditResponseModel getPostEditDetails(@PathVariable UUID id) {
        return postService.getPostEditDetails(id);
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/all")
    public PostsResponseModel all(Pageable pageable) {
        return postService.allPosts(pageable);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/subscribed")
    public PostsResponseModel getUserSubscribedPosts(@AuthenticationPrincipal User user,
                                                     Pageable pageable) {
        return postService.subscribedPosts(user, pageable);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/default-posts")
    public PostsResponseModel getPostsFromTheDefaultSubreddits(Pageable pageable) {
        return postService.defaultPosts(pageable);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/user/{username}")
    public PostsResponseModel findAllByUsername(@PathVariable String username, Pageable pageable) {
        return postService.findAllByUsername(username, pageable);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/subreddit/{subreddit}")
    public PostsResponseModel findAllBySubreddit(@PathVariable String subreddit, Pageable pageable) {
        return postService.findAllBySubreddit(subreddit.toLowerCase(), pageable);
    }
}
