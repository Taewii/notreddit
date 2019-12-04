package notreddit.services;

import notreddit.SingletonModelMapper;
import notreddit.domain.entities.*;
import notreddit.domain.models.responses.post.PostDetailsResponseModel;
import notreddit.domain.models.responses.post.PostListResponseModel;
import notreddit.domain.models.responses.post.PostsResponseModel;
import notreddit.repositories.*;
import notreddit.web.exceptions.AccessForbiddenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
            post.setComments(List.of(new Comment()));

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
        cloudStorage = mock(CloudStorage.class);
        thumbnailService = mock(ThumbnailService.class);
        subredditRepository = mock(SubredditRepository.class);
        postRepository = mock(PostRepository.class);
        voteRepository = mock(VoteRepository.class);
        commentRepository = mock(CommentRepository.class);
        mentionRepository = mock(MentionRepository.class);
        userRepository = mock(UserRepository.class);
        postService = new PostServiceImpl(subredditRepository, postRepository, cloudStorage, thumbnailService,
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
    void allPosts_withExistingPosts_returnsCorrectlyMappedObjects() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.findAll(any(Pageable.class))).thenReturn(createPosts(3, pageable));

        PostsResponseModel response = postService.allPosts(pageable);
        assertPostResponseModel(response);
    }

    @Test
    void allPosts_withNoExistingPosts_returnsCorrectObject() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.findAll(any(Pageable.class))).thenReturn(createPosts(0, pageable));

        PostsResponseModel response = postService.allPosts(pageable);

        assertEquals(0, response.getTotal());
        assertTrue(response.getPosts().isEmpty());
    }

    @Test
    void findAllByUsername_withExistingPosts_returnsCorrectlyMappedObjects() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.findAllByUsername(any(String.class), any(Pageable.class)))
                .thenReturn(createPosts(3, pageable));

        PostsResponseModel response = postService.findAllByUsername("username", pageable);
        assertPostResponseModel(response);
    }

    @Test
    void findAllByUsername_withNoExistingPosts_returnsCorrectObject() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.findAllByUsername(any(String.class), any(Pageable.class)))
                .thenReturn(createPosts(0, pageable));

        PostsResponseModel response = postService.findAllByUsername("username", pageable);

        assertEquals(0, response.getTotal());
        assertTrue(response.getPosts().isEmpty());
    }

    @Test
    void findAllBySubreddit_withExistingPosts_returnsCorrectlyMappedObjects() {
        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.getPostIdsBySubredditTitle(any(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(UUID.randomUUID())));
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
        when(user.getSubscriptions()).thenReturn(Set.of(new Subreddit()));
        when(userRepository.getWithSubscriptions(any(User.class))).thenReturn(user);

        Pageable pageable = PageRequest.of(0, 3, Sort.unsorted());
        when(postRepository.getSubscribedPostsIds(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(UUID.randomUUID())));
        when(postRepository.getPostsFromIdList(any(), any(Sort.class)))
                .thenReturn(createPosts(3, pageable).getContent());

        PostsResponseModel response = postService.subscribedPosts(user, pageable);
        assertPostResponseModel(response);
    }

    @Test
    void subscribedPosts_withNoExistingPosts_returnsCorrectObject() {
        User user = mock(User.class);
        when(user.getSubscriptions()).thenReturn(Set.of(new Subreddit()));
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
                .thenReturn(new PageImpl<>(List.of(UUID.randomUUID())));
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
                .thenReturn(new PageImpl<>(List.of(UUID.randomUUID().toString())));
        when(postRepository.getPostsFromIdList(any(), any(Sort.class)))
                .thenReturn(createPosts(3, pageable).getContent());

        PostsResponseModel response = postService.getPostsByVoteChoice(user, "username", 1, pageable);
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

        PostsResponseModel response = postService.getPostsByVoteChoice(user, "username", 1, pageable);

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
                () -> postService.getPostsByVoteChoice(user, "otherUsername", 1, pageable));
    }

    @Test
    void create() {
    }

    @Test
    void delete() {
    }
}