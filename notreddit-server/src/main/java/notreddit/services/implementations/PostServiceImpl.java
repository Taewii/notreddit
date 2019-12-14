package notreddit.services.implementations;

import lombok.RequiredArgsConstructor;
import notreddit.constants.ApiResponseMessages;
import notreddit.constants.ErrorMessages;
import notreddit.constants.GeneralConstants;
import notreddit.domain.entities.File;
import notreddit.domain.entities.Post;
import notreddit.domain.entities.Subreddit;
import notreddit.domain.entities.User;
import notreddit.domain.models.requests.PostCreateRequest;
import notreddit.domain.models.requests.PostEditRequest;
import notreddit.domain.models.responses.api.ApiResponse;
import notreddit.domain.models.responses.post.PostDetailsResponseModel;
import notreddit.domain.models.responses.post.PostEditResponseModel;
import notreddit.domain.models.responses.post.PostListResponseModel;
import notreddit.domain.models.responses.post.PostsResponseModel;
import notreddit.repositories.*;
import notreddit.services.CloudStorage;
import notreddit.services.PostService;
import notreddit.services.ThumbnailService;
import notreddit.web.exceptions.AccessForbiddenException;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static notreddit.constants.GeneralConstants.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final String MODERATOR_ROLE = "ROLE_MODERATOR";

    private final CloudStorage cloudStorage;
    private final ThumbnailService thumbnailService;
    private final SubredditRepository subredditRepository;
    private final PostRepository postRepository;
    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final MentionRepository mentionRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    @Caching(evict = {
            @CacheEvict(value = POSTS_BY_ID_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_USERNAME_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_SUBREDDIT_CACHE, allEntries = true),
            @CacheEvict(value = SUBSCRIBED_POSTS_CACHE, allEntries = true),
            @CacheEvict(value = SUBREDDITS_WITH_POST_AND_SUBSCRIBER_COUNT_CACHE, allEntries = true)
    })
    public ResponseEntity<?> create(PostCreateRequest request, User creator) {
        Subreddit subreddit = subredditRepository.findByTitleIgnoreCase(request.getSubreddit()).orElse(null);

        if (subreddit == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, ApiResponseMessages.NONEXISTENT_SUBREDDIT));
        }

        Post post = mapper.map(request, Post.class);
        post.setCreator(creator);
        post.setSubreddit(subreddit);
        post.setCreatedOn(LocalDateTime.now());

        if (request.getFile() == null && request.getUrl().isEmpty()) { // text upload
            return createPostWithoutFiles(post);
        } else if (request.getFile() != null && request.getUrl().isEmpty()) { // file upload
            return createPostWithUploadedFile(request, post);
        } else if (request.getFile() == null && !request.getUrl().isEmpty()) { // url upload
            return createPostWithWebUrl(request, post);
        } else { // file and url -> not allowed
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, ApiResponseMessages.ONLY_ONE_UPLOADED_METHOD_ALLOWED));
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = POSTS_BY_ID_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_USERNAME_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_SUBREDDIT_CACHE, allEntries = true),
            @CacheEvict(value = SUBSCRIBED_POSTS_CACHE, allEntries = true),
            @CacheEvict(value = SUBREDDITS_WITH_POST_AND_SUBSCRIBER_COUNT_CACHE, allEntries = true)
    })
    public ResponseEntity<?> edit(PostEditRequest request, User user) {
        Post post = postRepository.findByIdWithFileAnSubreddit(request.getPostId()).orElse(null);
        if (post == null || !post.getCreator().getUsername().equalsIgnoreCase(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, ApiResponseMessages.NONEXISTENT_POST_OR_NOT_CREATOR));
        }

        Subreddit subreddit = subredditRepository.findByTitleIgnoreCase(request.getSubreddit()).orElse(null);
        if (subreddit == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, ApiResponseMessages.NONEXISTENT_SUBREDDIT));
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setSubreddit(subreddit);

        // no post, url, but post has existing file uploaded
        if (request.getFile() == null && request.getUrl().isEmpty() && !request.isHasUploadedFile()) {
            post.setFile(null);
        } else if (request.getFile() != null && request.getUrl().isEmpty()) { // file upload
            editPostWithUploadedFile(request, post);
        } else if (request.getFile() == null && !request.getUrl().isEmpty()) { // url upload
            editPostWithWebUrl(request, post);
        } else if (request.getFile() != null && !request.getUrl().isEmpty()) { // file and url -> not allowed
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, ApiResponseMessages.ONLY_ONE_UPLOADED_METHOD_ALLOWED));
        }

        postRepository.saveAndFlush(post);
        return ResponseEntity
                .ok(new ApiResponse(true, ApiResponseMessages.SUCCESSFUL_POST_EDITION));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = POSTS_BY_ID_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_USERNAME_CACHE, allEntries = true),
            @CacheEvict(value = POSTS_BY_SUBREDDIT_CACHE, allEntries = true),
            @CacheEvict(value = SUBSCRIBED_POSTS_CACHE, allEntries = true)
    })
    public ResponseEntity<?> delete(UUID postId, User user) {
        Post post = postRepository.findByIdWithCreatorAndComments(postId).orElse(null);

        if (post == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, ApiResponseMessages.NONEXISTENT_POST));
        }

        boolean isCreatorOrModerator = user.getUsername().equals(post.getCreator().getUsername()) ||
                user.getRoles().stream().anyMatch(r -> r.getAuthority().equals(MODERATOR_ROLE));

        if (!isCreatorOrModerator) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, ErrorMessages.ACCESS_FORBIDDEN));
        }

        voteRepository.deleteAllByPostId(post.getId());
        post.getComments()
                .parallelStream()
                .forEach(comment -> {
                    mentionRepository.deleteAllByCommentId(comment.getId());
                    voteRepository.deleteAllByCommentId(comment.getId());
                    commentRepository.deleteById(comment.getId());
                });

        if (post.getFile() != null) {
            String fileUrl = post.getFile().getUrl();
            if (fileUrl.contains("dropbox")) { // if the file is uploaded to the cloud storage -> delete it
                cloudStorage.removeFile(fileUrl);
            }
        }

        postRepository.delete(post);
        return ResponseEntity
                .ok(new ApiResponse(true, ApiResponseMessages.SUCCESSFUL_POST_DELETION));
    }

    @Override
    @Cacheable(value = POSTS_BY_ID_CACHE, key = "#id")
    public PostDetailsResponseModel findById(UUID id) {
        Post post = postRepository.findByIdEager(id).orElseThrow(NoSuchElementException::new);
        return mapper.map(post, PostDetailsResponseModel.class);
    }

    @Override
    public PostEditResponseModel getPostEditDetails(UUID id) {
        Post post = postRepository.findByIdWithFileAnSubreddit(id).orElseThrow(NoSuchElementException::new);
        return mapper.map(post, PostEditResponseModel.class);
    }

    @Override
    public PostsResponseModel allPosts(Pageable pageable) {
        Page<Post> allPosts = postRepository.findAllPageable(pageable);
        return getPostsResponseModel(allPosts);
    }

    @Override
    public PostsResponseModel defaultPosts(Pageable pageable) {
        Set<Subreddit> defaultSubreddits = subredditRepository.findByTitleIn(GeneralConstants.DEFAULT_SUBREDDITS);
        Page<UUID> subscribedPostsIds = postRepository.getSubscribedPostsIds(defaultSubreddits, pageable);
        List<Post> subscribedPosts = getPostsOrEmptyList(subscribedPostsIds.getContent(), pageable);

        return getPostsResponseModel(subscribedPostsIds.getTotalElements(), subscribedPosts);
    }

    @Override
    @Cacheable(value = SUBSCRIBED_POSTS_CACHE, keyGenerator = "pageableKeyGenerator")
    public PostsResponseModel subscribedPosts(User user, Pageable pageable) {
        user = userRepository.getWithSubscriptions(user);
        /* Using two queries to avoid
         * Hibernate “HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!”
         * warning message, taken from https://vladmihalcea.com/fix-hibernate-hhh000104-entity-fetch-pagination-warning-message/
         */
        Page<UUID> subscribedPostsIds = postRepository.getSubscribedPostsIds(user.getSubscriptions(), pageable);
        List<Post> subscribedPosts = getPostsOrEmptyList(subscribedPostsIds.getContent(), pageable);

        return getPostsResponseModel(subscribedPostsIds.getTotalElements(), subscribedPosts);
    }

    @Override
    @Cacheable(value = POSTS_BY_USERNAME_CACHE, keyGenerator = "pageableKeyGenerator")
    public PostsResponseModel findAllByUsername(String username, Pageable pageable) {
        Page<Post> allByUsername = postRepository.findAllByUsername(username.toLowerCase(), pageable);
        return getPostsResponseModel(allByUsername);
    }

    @Override
    @Cacheable(value = POSTS_BY_SUBREDDIT_CACHE, keyGenerator = "pageableKeyGenerator")
    public PostsResponseModel findAllBySubreddit(String subreddit, Pageable pageable) {
        Page<UUID> allBySubredditTitle = postRepository.getPostIdsBySubredditTitle(subreddit.toLowerCase(), pageable);
        List<Post> postsBySubreddit = getPostsOrEmptyList(allBySubredditTitle.getContent(), pageable);

        return getPostsResponseModel(allBySubredditTitle.getTotalElements(), postsBySubreddit);
    }

    @Override
    public PostsResponseModel findPostsByVoteChoice(User user, String username, int choice, Pageable pageable) {
        if (!user.getUsername().equalsIgnoreCase(username)) {
            throw new AccessForbiddenException(ErrorMessages.ACCESS_FORBIDDEN);
        }

        Pageable pageRequest = convertToNativePageRequest(pageable);
        Page<String> postIdsByVoteChoice = postRepository.findPostIdsByUserAndVoteChoice(user.getId(), (byte) choice, pageRequest);
        List<UUID> postIds = postIdsByVoteChoice.getContent().stream().map(UUID::fromString).collect(Collectors.toList());
        List<Post> posts = getPostsOrEmptyList(postIds, pageable);

        return getPostsResponseModel(postIdsByVoteChoice.getTotalElements(), posts);
    }

    private ResponseEntity<?> createPostWithoutFiles(Post post) {
        postRepository.saveAndFlush(post);
        return getCreatedResponseEntityWithPath();
    }

    private ResponseEntity<?> createPostWithUploadedFile(PostCreateRequest request, Post post) {
        double fileSizeInMb = request.getFile().getSize() / 1024d / 1024d;
        if (fileSizeInMb > 10) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, ApiResponseMessages.FILE_SIZE_OVER_10MB));
        }

        File file = uploadFile(request.getFile());
        post.addFile(file);
        postRepository.saveAndFlush(post);

        return getCreatedResponseEntityWithPath();
    }

    private ResponseEntity<?> createPostWithWebUrl(PostCreateRequest request, Post post) {
        File file = new File();
        file.setUrl(request.getUrl());
        file.setThumbnailUrl(thumbnailService.generateThumbnailUrl(request.getUrl()));

        post.addFile(file);
        postRepository.saveAndFlush(post);

        return getCreatedResponseEntityWithPath();
    }

    private void editPostWithWebUrl(PostEditRequest request, Post post) {
        File file = new File();

        if (post.getFile() != null) {
            file = post.getFile();
        } else {
            post.addFile(file);
        }

        file.setUrl(request.getUrl());
        file.setThumbnailUrl(thumbnailService.generateThumbnailUrl(request.getUrl()));
    }

    private void editPostWithUploadedFile(PostEditRequest request, Post post) {
        if (post.getFile() != null) {
            File file = post.getFile();
            Map<String, Object> params = cloudStorage.updateFile(request.getFile(), post.getFile().getUrl());
            String fileUrl = params.get("url").toString();

            file.setUrl(fileUrl);

            // if file is not an image -> create thumbnail, else use the image
            if (params.get("contentType").toString().contains("image")) {
                file.setThumbnailUrl(fileUrl);
            } else {
                file.setThumbnailUrl(thumbnailService.generateThumbnailUrl(fileUrl));
            }
        } else {
            File file = uploadFile(request.getFile());
            post.addFile(file);
        }
    }

    private File uploadFile(MultipartFile multipartFile) {
        Map<String, Object> params = cloudStorage.uploadFileAndGetParams(multipartFile);
        String fileUrl = params.get("url").toString();

        File file = new File();
        file.setUrl(fileUrl);

        if (params.get("contentType").toString().contains("image")) {
            file.setThumbnailUrl(fileUrl);
        } else {
            file.setThumbnailUrl(thumbnailService.generateThumbnailUrl(fileUrl));
        }

        return file;
    }

    private ResponseEntity<?> getCreatedResponseEntityWithPath() {
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/post/create")
                .buildAndExpand().toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true, ApiResponseMessages.SUCCESSFUL_POST_CREATION));
    }

    private PostsResponseModel getPostsResponseModel(Page<Post> allByUsername) {
        List<PostListResponseModel> posts = allByUsername.stream()
                .map(p -> {
                    PostListResponseModel model = mapper.map(p, PostListResponseModel.class);
                    model.setCommentCount(p.getComments().size());
                    return model;
                })
                .collect(Collectors.toList());

        return new PostsResponseModel(allByUsername.getTotalElements(), posts);
    }

    private PostsResponseModel getPostsResponseModel(long total, List<Post> posts) {
        List<PostListResponseModel> mappedPosts = posts.stream()
                .map(p -> {
                    PostListResponseModel model = mapper.map(p, PostListResponseModel.class);
                    model.setCommentCount(p.getComments().size());
                    return model;
                })
                .collect(Collectors.toList());

        return new PostsResponseModel(total, mappedPosts);
    }

    private Pageable convertToNativePageRequest(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            Sort.Order createdOn = pageable.getSort().getOrderFor("createdOn");
            if (createdOn != null) {
                Sort createdOnNativeQuery = Sort.by("created_on");
                if (createdOn.getDirection().isAscending()) {
                    createdOnNativeQuery = createdOnNativeQuery.ascending();
                } else {
                    createdOnNativeQuery = createdOnNativeQuery.descending();
                }
                return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), createdOnNativeQuery);
            }
        }
        return pageable;
    }

    private List<Post> getPostsOrEmptyList(List<UUID> postIds, Pageable pageable) {
        if (postIds.isEmpty()) return new ArrayList<>();
        return postRepository.getPostsFromIdList(postIds, pageable.getSort());
    }
}
