package notreddit.services;

import notreddit.SingletonModelMapper;
import notreddit.domain.entities.*;
import notreddit.domain.enums.Authority;
import notreddit.domain.models.requests.PostCreateRequest;
import notreddit.domain.models.requests.PostEditRequest;
import notreddit.domain.models.responses.post.PostDetailsResponseModel;
import notreddit.domain.models.responses.post.PostEditResponseModel;
import notreddit.domain.models.responses.post.PostListResponseModel;
import notreddit.domain.models.responses.post.PostsResponseModel;
import notreddit.repositories.*;
import notreddit.services.implementations.PostServiceImpl;
import notreddit.web.exceptions.AccessForbiddenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceImplTest {

    private PostService postService;
    private CloudStorage cloudStorage;
    private ThumbnailService thumbnailService;
    private SubredditRepository subredditRepository;
    private PostRepository postRepository;
    private VoteRepository voteRepository;
    private CommentRepository commentRepository;
    private MentionRepository mentionRepository;
    private UserRepository userRepository;

    private Page<Post> createPosts(int count, Pageable pageable) {
        List<Post> posts = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setUsername("username" + i);
            File file = new File();
            file.setThumbnailUrl("thumbnailUrl" + i);
            Subreddit subreddit = new Subreddit();
            subreddit.setTitle("subredditTitle" + i);

            Post post = new Post();
            post.setId(UUID.randomUUID());
            post.setTitle("postTitle" + i);
            post.setUpvotes(i);
            post.setDownvotes(i);
            post.setCreatedOn(LocalDateTime.now());
            post.setCreator(user);
            post.setFile(file);
            post.setSubreddit(subreddit);
            post.setComments(Collections.singletonList(new Comment()));

            posts.add(post);
        }

        return new PageImpl<>(posts, pageable, posts.size());
    }

    private void assertPostResponseModel(PostsResponseModel model) {
        List<PostListResponseModel> posts = (List<PostListResponseModel>) model.getPosts();

        assertEquals(3, posts.size());
        for (int i = 0; i < posts.size(); i++) {
            assertTrue(posts.get(i).getCreatedOn() > 0);
            assertEquals(i, posts.get(i).getDownvotes());
            assertEquals(i, posts.get(i).getUpvotes());
            assertEquals(1, posts.get(i).getCommentCount());
            assertEquals("username" + i, posts.get(i).getCreatorUsername());
            assertEquals("thumbnailUrl" + i, posts.get(i).getFileThumbnailUrl());
            assertEquals("subredditTitle" + i, posts.get(i).getSubredditTitle());
            assertEquals("postTitle" + i, posts.get(i).getTitle());
        }
    }

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        cloudStorage = mock(CloudStorage.class);
        thumbnailService = mock(ThumbnailService.class);
        subredditRepository = mock(SubredditRepository.class);
        postRepository = mock(PostRepository.class);
        voteRepository = mock(VoteRepository.class);
        commentRepository = mock(CommentRepository.class);
        mentionRepository = mock(MentionRepository.class);
        userRepository = mock(UserRepository.class);
        postService = new PostServiceImpl(cloudStorage, thumbnailService, subredditRepository, postRepository,
                voteRepository, commentRepository, mentionRepository, userRepository, SingletonModelMapper.mapper());
    }

    @Test
    void findById_withExistingPost_returnsCorrectlyMappedObject() {
        Post post = new Post();
        post.setContent("content");
        File file = new File();
        file.setUrl("fileUrl");
        post.setFile(file);

        when(postRepository.findByIdEager(any(UUID.class))).thenReturn(Optional.of(post));

        PostDetailsResponseModel result = postService.findById(UUID.randomUUID());

        assertEquals(PostDetailsResponseModel.class, result.getClass());
        assertEquals("content", result.getContent());
        assertEquals("fileUrl", result.getFileUrl());
    }

    @Test
    void findById_withNonExistingPost_throwsNoSuchElementException() {
        when(postRepository.findByIdEager(any(UUID.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(NoSuchElementException.class, () -> postService.findById(UUID.randomUUID()));
    }

    @Test
    void getPostEditDetails_withExistingPost_returnsCorrectlyMappedObject() {
        UUID id = UUID.randomUUID();
        File file = new File();
        file.setUrl("fileUrl");
        Subreddit subreddit = new Subreddit();
        subreddit.setTitle("subredditTitle");
        Post post = new Post();
        post.setId(id);
        post.setContent("content");
        post.setFile(file);
        post.setSubreddit(subreddit);

        when(postRepository.findByIdWithFileAnSubreddit(any(UUID.class))).thenReturn(Optional.of(post));

        PostEditResponseModel result = postService.getPostEditDetails(id);

        assertEquals(PostEditResponseModel.class, result.getClass());
        assertEquals(id.toString(), result.getId());
        assertEquals("content", result.getContent());
        assertEquals("fileUrl", result.getFileUrl());
        assertEquals("subredditTitle", result.getSubredditTitle());
    }

    @Test
    void getPostEditDetails_withNonExistingPost_throwsNoSuchElementException() {
        when(postRepository.findByIdWithFileAnSubreddit(any(UUID.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(NoSuchElementException.class, () -> postService.getPostEditDetails(UUID.randomUUID()));
    }

    @Test
    void allPosts_withExistingPosts_returnsCorrectlyMappedObjects() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.findAllPageable(any(Pageable.class))).thenReturn(createPosts(3, pageable));

        PostsResponseModel response = postService.allPosts(pageable);
        assertPostResponseModel(response);
    }

    @Test
    void allPosts_withNoExistingPosts_returnsCorrectObject() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.findAllPageable(any(Pageable.class))).thenReturn(createPosts(0, pageable));

        PostsResponseModel response = postService.allPosts(pageable);

        assertEquals(0, response.getTotal());
        assertTrue(response.getPosts().isEmpty());
    }

    @Test
    void findAllByUsername_withExistingPosts_returnsCorrectlyMappedObjects() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.findAllPostIdsByUsername(any(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(UUID.randomUUID())));
        when(postRepository.getPostsFromIdList(any(), any(Sort.class)))
                .thenReturn(createPosts(3, pageable).getContent());

        PostsResponseModel response = postService.findAllByUsername("username", pageable);
        assertPostResponseModel(response);
    }

    @Test
    void findAllByUsername_withNoExistingPosts_returnsCorrectObject() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.findAllPostIdsByUsername(any(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        when(postRepository.getPostIdsBySubredditTitle(any(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        PostsResponseModel response = postService.findAllByUsername("username", pageable);

        assertEquals(0, response.getTotal());
        assertTrue(response.getPosts().isEmpty());
    }

    @Test
    void findAllBySubreddit_withExistingPosts_returnsCorrectlyMappedObjects() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.getPostIdsBySubredditTitle(any(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(UUID.randomUUID())));
        when(postRepository.getPostsFromIdList(any(), any(Sort.class)))
                .thenReturn(createPosts(3, pageable).getContent());

        PostsResponseModel response = postService.findAllBySubreddit("subreddit", pageable);
        assertPostResponseModel(response);
    }

    @Test
    void findAllBySubreddit_withNoExistingPosts_returnsCorrectObject() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.getPostIdsBySubredditTitle(any(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        PostsResponseModel response = postService.findAllBySubreddit("subreddit", pageable);

        assertEquals(0, response.getTotal());
        assertTrue(response.getPosts().isEmpty());
    }

    @Test
    void subscribedPosts_withExistingPosts_returnsCorrectlyMappedObjects() {
        User user = mock(User.class);
        when(user.getSubscriptions()).thenReturn(new HashSet<>(Collections.singletonList(new Subreddit())));
        when(userRepository.getWithSubscriptions(any(User.class))).thenReturn(user);

        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.getSubscribedPostsIds(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(UUID.randomUUID())));
        when(postRepository.getPostsFromIdList(any(), any(Sort.class)))
                .thenReturn(createPosts(3, pageable).getContent());

        PostsResponseModel response = postService.subscribedPosts(user, pageable);
        assertPostResponseModel(response);
    }

    @Test
    void subscribedPosts_withNoExistingPosts_returnsCorrectObject() {
        User user = mock(User.class);
        when(user.getSubscriptions()).thenReturn(new HashSet<>(Collections.singletonList(new Subreddit())));
        when(userRepository.getWithSubscriptions(any(User.class))).thenReturn(user);

        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.getSubscribedPostsIds(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        when(postRepository.getPostsFromIdList(any(), any(Sort.class)))
                .thenReturn(createPosts(0, pageable).getContent());

        PostsResponseModel response = postService.subscribedPosts(user, pageable);

        assertEquals(0, response.getTotal());
        assertTrue(response.getPosts().isEmpty());
    }

    @Test
    void defaultPosts_withExistingPosts_returnsCorrectlyMappedObjects() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(subredditRepository.findByTitleIn(any())).thenReturn(new HashSet<>());
        when(postRepository.getSubscribedPostsIds(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(UUID.randomUUID())));
        when(postRepository.getPostsFromIdList(any(), any(Sort.class)))
                .thenReturn(createPosts(3, pageable).getContent());

        PostsResponseModel response = postService.defaultPosts(pageable);
        assertPostResponseModel(response);
    }

    @Test
    void defaultPosts_withNoExistingPosts_returnsCorrectObject() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.getSubscribedPostsIds(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        when(postRepository.getPostsFromIdList(any(), any(Sort.class)))
                .thenReturn(createPosts(0, pageable).getContent());

        PostsResponseModel response = postService.defaultPosts(pageable);

        assertEquals(0, response.getTotal());
        assertTrue(response.getPosts().isEmpty());
    }

    @Test
    void getPostsByVoteChoice_withExistingPosts_returnsCorrectlyMappedObjects() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdOn"));
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(user.getUsername()).thenReturn("username");

        when(postRepository.findPostIdsByUserAndVoteChoice(any(UUID.class), any(byte.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(UUID.randomUUID().toString())));
        when(postRepository.getPostsFromIdList(any(), any(Sort.class)))
                .thenReturn(createPosts(3, pageable).getContent());

        PostsResponseModel response = postService.findPostsByVoteChoice(user, "username", 1, pageable);
        assertPostResponseModel(response);
    }

    @Test
    void getPostsByVoteChoice_withNoExistingPosts_returnsCorrectObject() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "createdOn"));
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(user.getUsername()).thenReturn("username");

        when(postRepository.findPostIdsByUserAndVoteChoice(any(UUID.class), any(byte.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        when(postRepository.getPostsFromIdList(any(), any(Sort.class)))
                .thenReturn(createPosts(0, pageable).getContent());

        PostsResponseModel response = postService.findPostsByVoteChoice(user, "username", 1, pageable);

        assertEquals(0, response.getTotal());
        assertTrue(response.getPosts().isEmpty());
    }

    @Test
    void getPostsByVoteChoice_withDifferentCurrentUsernameAndWantedUsername_throwsAccessForbiddenException() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(user.getUsername()).thenReturn("username");

        Assertions.assertThrows(AccessForbiddenException.class,
                () -> postService.findPostsByVoteChoice(user, "otherUsername", 1, pageable));
    }

    @Test
    void create_withMultipartFile_shouldInvokeAllNeededMethods() {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("");
        request.setFile(new MockMultipartFile("name", new byte[1]));

        Map<String, Object> cloudStorageParams = new HashMap<>();
        cloudStorageParams.put("url", "url");
        cloudStorageParams.put("contentType", "text/html");

        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));
        when(cloudStorage.uploadFileAndGetParams(any(MultipartFile.class))).thenReturn(cloudStorageParams);

        ResponseEntity<?> response = postService.create(request, new User());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(cloudStorage).uploadFileAndGetParams(any(MultipartFile.class));
        verify(thumbnailService).generateThumbnailUrl(cloudStorageParams.get("url").toString());
        verify(postRepository).saveAndFlush(any(Post.class));
    }

    @Test
    void create_withMultipartFileThatIsNotAnImage_shouldInvokeAllNeededMethods() {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("");
        request.setFile(new MockMultipartFile("name", new byte[1]));

        Map<String, Object> cloudStorageParams = new HashMap<>();
        cloudStorageParams.put("url", "url");
        cloudStorageParams.put("contentType", "text/html");

        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));
        when(cloudStorage.uploadFileAndGetParams(any(MultipartFile.class))).thenReturn(cloudStorageParams);

        ResponseEntity<?> response = postService.create(request, new User());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(cloudStorage).uploadFileAndGetParams(any(MultipartFile.class));
        verify(thumbnailService).generateThumbnailUrl(cloudStorageParams.get("url").toString());
        verify(postRepository).saveAndFlush(any(Post.class));
    }

    @Test
    void create_withMultipartFileThatInAnImage_shouldInvokeAllNeededMethods() {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("");
        request.setFile(new MockMultipartFile("name", new byte[1]));

        Map<String, Object> cloudStorageParams = new HashMap<>();
        cloudStorageParams.put("url", "url");
        cloudStorageParams.put("contentType", "image/jpeg");

        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));
        when(cloudStorage.uploadFileAndGetParams(any(MultipartFile.class))).thenReturn(cloudStorageParams);

        ResponseEntity<?> response = postService.create(request, new User());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(cloudStorage).uploadFileAndGetParams(any(MultipartFile.class));
        verify(postRepository).saveAndFlush(any(Post.class));
        verify(thumbnailService, never()).generateThumbnailUrl(cloudStorageParams.get("url").toString());
    }

    @Test
    void create_withMultipartFileThatIsLargerThant10MB_shouldDoNothing() {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("");
        request.setFile(new MockMultipartFile("name", new byte[11 * 1024 * 1024]));

        Map<String, Object> cloudStorageParams = new HashMap<>();
        cloudStorageParams.put("url", "url");
        cloudStorageParams.put("contentType", "image/jpeg");

        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));
        when(cloudStorage.uploadFileAndGetParams(any(MultipartFile.class))).thenReturn(cloudStorageParams);

        ResponseEntity<?> response = postService.create(request, new User());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(cloudStorage, never()).uploadFileAndGetParams(any(MultipartFile.class));
        verify(postRepository, never()).saveAndFlush(any(Post.class));
        verify(thumbnailService, never()).generateThumbnailUrl(cloudStorageParams.get("url").toString());
    }

    @Test
    void create_withUrl_shouldInvokeAllNeededMethods() {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("url");

        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));

        ResponseEntity<?> response = postService.create(request, new User());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(thumbnailService).generateThumbnailUrl(request.getUrl());
        verify(postRepository).saveAndFlush(any(Post.class));
    }

    @Test
    void create_withoutFileNorUrl_shouldInvokeAllNeededMethods() {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("");

        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));

        ResponseEntity<?> response = postService.create(request, new User());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(thumbnailService, never()).generateThumbnailUrl(request.getUrl());
        verify(postRepository).saveAndFlush(any(Post.class));
    }

    @Test
    void create_withNonExistingSubreddit_shouldDoNothing() {
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = postService.create(new PostCreateRequest(), new User());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(postRepository, never()).saveAndFlush(any(Post.class));
    }

    @Test
    void create_withBothFileAndUrl_shouldDoNothing() {
        PostCreateRequest request = new PostCreateRequest();
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("url");
        request.setFile(new MockMultipartFile("name", new byte[11 * 1024 * 1024]));

        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));

        ResponseEntity<?> response = postService.create(request, new User());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(postRepository, never()).saveAndFlush(any(Post.class));
    }

    @Test
    void edit_withMultipartFileThatIsAnImageAndPostWithExistingFile_shouldInvokeAllNeededMethods() {
        PostEditRequest request = new PostEditRequest();
        request.setPostId(UUID.randomUUID());
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("");
        request.setFile(new MockMultipartFile("name", new byte[1]));

        User user = new User();
        user.setUsername("username");
        File file = new File();
        file.setUrl("fileUrl");
        Post post = new Post();
        post.setFile(file);
        post.setCreator(user);

        Map<String, Object> cloudStorageParams = new HashMap<>();
        cloudStorageParams.put("url", "url");
        cloudStorageParams.put("contentType", "text/html");

        when(postRepository.findByIdWithFileAnSubreddit(any(UUID.class))).thenReturn(Optional.of(post));
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));
        when(cloudStorage.updateFile(any(MultipartFile.class), any(String.class))).thenReturn(cloudStorageParams);

        ResponseEntity<?> response = postService.edit(request, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cloudStorage).updateFile(any(MultipartFile.class), any(String.class));
        verify(thumbnailService).generateThumbnailUrl(cloudStorageParams.get("url").toString());
        verify(postRepository).saveAndFlush(any(Post.class));
    }

    @Test
    void edit_withMultipartFileThatNotIsAnImageAndPostWithExistingFile_shouldInvokeAllNeededMethods() {
        PostEditRequest request = new PostEditRequest();
        request.setPostId(UUID.randomUUID());
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("");
        request.setFile(new MockMultipartFile("name", new byte[1]));

        User user = new User();
        user.setUsername("username");
        File file = new File();
        file.setUrl("fileUrl");
        Post post = new Post();
        post.setFile(file);
        post.setCreator(user);

        Map<String, Object> cloudStorageParams = new HashMap<>();
        cloudStorageParams.put("url", "url");
        cloudStorageParams.put("contentType", "image/jpg");

        when(postRepository.findByIdWithFileAnSubreddit(any(UUID.class))).thenReturn(Optional.of(post));
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));
        when(cloudStorage.updateFile(any(MultipartFile.class), any(String.class))).thenReturn(cloudStorageParams);

        ResponseEntity<?> response = postService.edit(request, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cloudStorage).updateFile(any(MultipartFile.class), any(String.class));
        verify(postRepository).saveAndFlush(any(Post.class));
        verify(thumbnailService, never()).generateThumbnailUrl(cloudStorageParams.get("url").toString());
    }

    @Test
    void edit_withMultipartFileThatNotIsAnImageAndPostWithNoExistingFile_shouldInvokeAllNeededMethods() {
        PostEditRequest request = new PostEditRequest();
        request.setPostId(UUID.randomUUID());
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("");
        request.setFile(new MockMultipartFile("name", new byte[1]));

        User user = new User();
        user.setUsername("username");
        Post post = new Post();
        post.setCreator(user);

        Map<String, Object> cloudStorageParams = new HashMap<>();
        cloudStorageParams.put("url", "url");
        cloudStorageParams.put("contentType", "image/jpg");

        when(postRepository.findByIdWithFileAnSubreddit(any(UUID.class))).thenReturn(Optional.of(post));
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));
        when(cloudStorage.uploadFileAndGetParams(any(MultipartFile.class))).thenReturn(cloudStorageParams);

        ResponseEntity<?> response = postService.edit(request, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(postRepository).saveAndFlush(any(Post.class));
        verify(cloudStorage).uploadFileAndGetParams(any(MultipartFile.class));
        verify(cloudStorage, never()).updateFile(any(MultipartFile.class), any(String.class));
        verify(thumbnailService, never()).generateThumbnailUrl(cloudStorageParams.get("url").toString());
    }

    @Test
    void edit_withUrlAndPostWithExistingFile_shouldInvokeAllNeededMethods() {
        PostEditRequest request = new PostEditRequest();
        request.setPostId(UUID.randomUUID());
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("fileUrl");

        User user = new User();
        user.setUsername("username");
        File file = new File();
        file.setUrl("fileUrl");
        Post post = new Post();
        post.setCreator(user);
        post.setFile(file);

        when(postRepository.findByIdWithFileAnSubreddit(any(UUID.class))).thenReturn(Optional.of(post));
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));

        ResponseEntity<?> response = postService.edit(request, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(thumbnailService).generateThumbnailUrl(request.getUrl());
        verify(postRepository).saveAndFlush(any(Post.class));
    }

    @Test
    void edit_withUrlAndPostWithNoExistingFile_shouldInvokeAllNeededMethods() {
        PostEditRequest request = new PostEditRequest();
        request.setPostId(UUID.randomUUID());
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("fileUrl");

        User user = new User();
        user.setUsername("username");
        Post post = new Post();
        post.setCreator(user);

        when(postRepository.findByIdWithFileAnSubreddit(any(UUID.class))).thenReturn(Optional.of(post));
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));

        ResponseEntity<?> response = postService.edit(request, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(thumbnailService).generateThumbnailUrl(request.getUrl());
        verify(postRepository).saveAndFlush(any(Post.class));
    }

    @Test
    void edit_withNoUrlOrFileWithExistingFile_shouldInvokeAllNeededMethods() {
        PostEditRequest request = new PostEditRequest();
        request.setPostId(UUID.randomUUID());
        request.setTitle("title");
        request.setContent("content");
        request.setSubreddit("subreddit");
        request.setUrl("");
        request.setHasUploadedFile(true);

        User user = new User();
        user.setUsername("username");
        Post post = new Post();
        post.setCreator(user);

        when(postRepository.findByIdWithFileAnSubreddit(any(UUID.class))).thenReturn(Optional.of(post));
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));

        ResponseEntity<?> response = postService.edit(request, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(postRepository).saveAndFlush(any(Post.class));
    }

    @Test
    void edit_withBothUrlAndFile_shouldDoNothing() {
        PostEditRequest request = new PostEditRequest();
        request.setPostId(UUID.randomUUID());
        request.setFile(new MockMultipartFile("name", new byte[1]));
        request.setUrl("url");
        request.setSubreddit("subreddit");
        User user = new User();
        user.setUsername("user");
        Post post = new Post();
        post.setCreator(user);

        when(postRepository.findByIdWithFileAnSubreddit(any(UUID.class))).thenReturn(Optional.of(post));
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.of(new Subreddit()));

        ResponseEntity<?> response = postService.edit(request, user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(postRepository, never()).saveAndFlush(any(Post.class));
    }

    @Test
    void edit_withNonExistingSubreddit_shouldDoNothing() {
        PostEditRequest request = new PostEditRequest();
        request.setPostId(UUID.randomUUID());
        request.setSubreddit("subreddit");
        User user = new User();
        user.setUsername("user");
        Post post = new Post();
        post.setCreator(user);

        when(postRepository.findByIdWithFileAnSubreddit(any(UUID.class))).thenReturn(Optional.of(post));
        when(subredditRepository.findByTitleIgnoreCase(any(String.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = postService.edit(request, user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(postRepository, never()).saveAndFlush(any(Post.class));
    }

    @Test
    void edit_withUserThatIsNotTheCreator_shouldDoNothing() {
        PostEditRequest request = new PostEditRequest();
        request.setPostId(UUID.randomUUID());
        User creator = new User();
        creator.setUsername("username");
        User user = new User();
        user.setUsername("user");
        Post post = new Post();
        post.setCreator(creator);

        when(postRepository.findByIdWithFileAnSubreddit(any(UUID.class))).thenReturn(Optional.of(post));

        ResponseEntity<?> response = postService.edit(request, user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(postRepository, never()).saveAndFlush(any(Post.class));
    }

    @Test
    void delete_withModeratorUser_shouldInvokeAllNeededMethods() {
        Role role = new Role();
        role.setAuthority(Authority.MODERATOR);

        User user = new User();
        user.setUsername("username");
        user.getRoles().add(role);

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());

        File file = new File();
        file.setUrl("dropboxFileUrl");

        Post post = new Post();
        post.setId(UUID.randomUUID());
        post.setCreator(new User());
        post.setFile(file);
        post.getComments().add(comment);

        when(postRepository.findByIdWithCreatorAndComments(any(UUID.class))).thenReturn(Optional.of(post));

        postService.delete(UUID.randomUUID(), user);

        verify(voteRepository).deleteAllByPostId(post.getId());
        verify(voteRepository).deleteAllByCommentId(comment.getId());
        verify(mentionRepository).deleteAllByCommentId(comment.getId());
        verify(commentRepository).deleteById(comment.getId());
        verify(cloudStorage).removeFile(file.getUrl());
        verify(postRepository).delete(post);
    }

    @Test
    void delete_withCreatorUser_shouldInvokeAllNeededMethods() {
        Role role = new Role();
        role.setAuthority(Authority.USER);

        User user = new User();
        user.setUsername("username");
        user.getRoles().add(role);

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());

        File file = new File();
        file.setUrl("dropboxFileUrl");

        Post post = new Post();
        post.setId(UUID.randomUUID());
        post.setCreator(user);
        post.setFile(file);
        post.getComments().add(comment);

        when(postRepository.findByIdWithCreatorAndComments(any(UUID.class))).thenReturn(Optional.of(post));

        postService.delete(UUID.randomUUID(), user);

        verify(voteRepository).deleteAllByPostId(post.getId());
        verify(voteRepository).deleteAllByCommentId(comment.getId());
        verify(mentionRepository).deleteAllByCommentId(comment.getId());
        verify(commentRepository).deleteById(comment.getId());
        verify(cloudStorage).removeFile(file.getUrl());
        verify(postRepository).delete(post);
    }

    @Test
    void delete_withUserThatIsNotModeratorAndNotTheCreator_shouldDoNothing() {
        Role role = new Role();
        role.setAuthority(Authority.USER);

        User user = new User();
        user.setUsername("username");
        user.getRoles().add(role);

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());

        File file = new File();
        file.setUrl("dropboxFileUrl");

        Post post = new Post();
        post.setId(UUID.randomUUID());
        post.setCreator(new User());
        post.setFile(file);
        post.getComments().add(comment);

        when(postRepository.findByIdWithCreatorAndComments(any(UUID.class))).thenReturn(Optional.of(post));

        postService.delete(UUID.randomUUID(), user);

        verify(voteRepository, never()).deleteAllByPostId(post.getId());
        verify(voteRepository, never()).deleteAllByCommentId(comment.getId());
        verify(mentionRepository, never()).deleteAllByCommentId(comment.getId());
        verify(commentRepository, never()).deleteById(comment.getId());
        verify(cloudStorage, never()).removeFile(file.getUrl());
        verify(postRepository, never()).delete(post);
    }

    @Test
    void delete_withNonExistingPost_shouldDoNothing() {
        when(postRepository.findByIdWithCreatorAndComments(any(UUID.class))).thenReturn(Optional.empty());

        postService.delete(UUID.randomUUID(), new User());

        verify(voteRepository, never()).deleteAllByPostId(any(UUID.class));
        verify(voteRepository, never()).deleteAllByCommentId(any(UUID.class));
        verify(mentionRepository, never()).deleteAllByCommentId(any(UUID.class));
        verify(commentRepository, never()).deleteById(any(UUID.class));
        verify(cloudStorage, never()).removeFile(any(String.class));
        verify(postRepository, never()).delete(any(Post.class));
    }
}