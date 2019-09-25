package notreddit.web.controllers;

import notreddit.domain.entities.User;
import notreddit.domain.models.requests.PostCreateRequest;
import notreddit.domain.models.responses.PostListResponseModel;
import notreddit.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @ModelAttribute PostCreateRequest request,
                                    @AuthenticationPrincipal User creator) {
        return postService.create(request, creator);
    }

    @GetMapping("/all")
    public List<PostListResponseModel> all() {
        return postService.allPosts();
    }
}
