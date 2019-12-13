package notreddit.web.controllers;

import notreddit.web.controllers.utils.AbstractIntegrationTest;
import notreddit.web.controllers.utils.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static notreddit.constants.ApiResponseMessages.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest extends AbstractIntegrationTest {

    @Test
    @WithMockCustomUser("admin")
    void create_withUrlFile_createsPostAndReturnsCorrectStatusAndMessage() throws Exception {
        mockMvc.perform(post("/api/post/create")
                .param("title", "title")
                .param("content", "content")
                .param("subreddit", "random")
                .param("url", "https://url.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_POST_CREATION)));

    }

    @Test
    @WithMockCustomUser("admin")
    void create_withNoFile_createsPostAndReturnsCorrectStatusAndMessage() throws Exception {
        mockMvc.perform(post("/api/post/create")
                .param("title", "title")
                .param("content", "content")
                .param("subreddit", "bjj")
                .param("url", ""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_POST_CREATION)));
    }

    @Test
    @WithMockCustomUser("admin")
    void create_withNonExistentSubreddit_returnsCorrectStatusAndMessage() throws Exception {
        mockMvc.perform(post("/api/post/create")
                .param("title", "title")
                .param("content", "content")
                .param("subreddit", "nonexistent")
                .param("url", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(NONEXISTENT_SUBREDDIT)));
    }

    @Test
    @WithAnonymousUser
    void create_withAnonymousUser_returnsCorrectStatusAndMessage() throws Exception {
        mockMvc.perform(post("/api/post/create")
                .param("title", "title")
                .param("content", "content")
                .param("subreddit", "nonexistent")
                .param("url", ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void edit_withNoFile_editsPostAndReturnsCorrectStatusAndMessage() throws Exception {
        String postId = "d007bf28-190b-4123-ae10-69b8e8bd226f";

        mockMvc.perform(patch("/api/post/edit")
                .param("postId", postId)
                .param("title", "title")
                .param("content", "content")
                .param("subreddit", "aww")
                .param("url", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_POST_EDITION)));
    }

    @Test
    @WithMockCustomUser("root")
    void edit_withUrlFile_editsPostAndReturnsCorrectStatusAndMessage() throws Exception {
        String postId = "d007bf28-190b-4123-ae10-69b8e8bd226f";

        mockMvc.perform(patch("/api/post/edit")
                .param("postId", postId)
                .param("title", "title")
                .param("content", "content")
                .param("subreddit", "aww")
                .param("url", "https://url.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_POST_EDITION)));
    }

    @Test
    @WithMockCustomUser("user")
    void edit_withUserThatIsNotTheCreator_returnsCorrectStatusAndMessage() throws Exception {
        String postId = "d007bf28-190b-4123-ae10-69b8e8bd226f";
        mockMvc.perform(patch("/api/post/edit")
                .param("postId", postId)
                .param("title", "title")
                .param("content", "content")
                .param("subreddit", "aww")
                .param("url", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(NONEXISTENT_POST_OR_NOT_CREATOR)));
    }

    @Test
    @WithMockCustomUser("root")
    void edit_withNonExistentSubreddit_returnsCorrectStatusAndMessage() throws Exception {
        String postId = "d007bf28-190b-4123-ae10-69b8e8bd226f";
        mockMvc.perform(patch("/api/post/edit")
                .param("postId", postId)
                .param("title", "title")
                .param("content", "content")
                .param("subreddit", "nonexistent")
                .param("url", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(NONEXISTENT_SUBREDDIT)));
    }

    @Test
    @WithAnonymousUser
    void edit_withAnonymousUser_returnsCorrectStatusAndMessage() throws Exception {
        String postId = "d007bf28-190b-4123-ae10-69b8e8bd226f";
        mockMvc.perform(patch("/api/post/edit")
                .param("postId", postId)
                .param("title", "title")
                .param("content", "content")
                .param("subreddit", "aww")
                .param("url", ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("moderator")
    void all() throws Exception {
        mockMvc.perform(get("/api/post/all?page=0&size=5&sort=downvotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(17)))
                .andExpect(jsonPath("$.posts", hasSize(5)));
    }

    @Test
    @WithMockCustomUser("user")
    void all_withUnauthorizedUser_returnsCorrectMessageAndStatus() throws Exception {
        mockMvc.perform(get("/api/post/all?page=0&size=5&sort=downvotes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void all_withAnonymousUser_returnsCorrectMessageAndStatus() throws Exception {
        mockMvc.perform(get("/api/post/all?page=0&size=5&sort=downvotes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void getUserSubscribedPosts() throws Exception {
        mockMvc.perform(get("/api/post/subscribed?page=0&size=5&sort=downvotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(12)))
                .andExpect(jsonPath("$.posts", hasSize(5)));
    }

    @Test
    @WithAnonymousUser
    void getUserSubscribedPosts_withAnonymousUser_returnsCorrectMessageAndStatus() throws Exception {
        mockMvc.perform(get("/api/post/subscribed?page=0&size=5&sort=downvotes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void getPostsFromTheDefaultSubreddits() throws Exception {
        mockMvc.perform(get("/api/post/default-posts?page=0&size=5&sort=downvotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(11)))
                .andExpect(jsonPath("$.posts", hasSize(5)));
    }

    @Test
    @WithAnonymousUser
    void findAllByUsername() throws Exception {
        mockMvc.perform(get("/api/post/user/root?page=0&size=5&sort=downvotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(12)))
                .andExpect(jsonPath("$.posts", hasSize(5)));
    }

    @Test
    @WithAnonymousUser
    void findAllBySubreddit() throws Exception {
        mockMvc.perform(get("/api/post/subreddit/aww?page=0&size=5&sort=downvotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(8)))
                .andExpect(jsonPath("$.posts", hasSize(5)));
    }

    @Test
    @WithAnonymousUser
    void findById() throws Exception {
        String postId = "d92e1999-fd40-4ed8-b72a-faa16b54da4f";
        mockMvc.perform(get("/api/post/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creatorUsername", is("root")))
                .andExpect(jsonPath("$.title", is("some hoe")))
                .andExpect(jsonPath("$.subredditTitle", is("aww")))
                .andExpect(jsonPath("$.upvotes", is(2)))
                .andExpect(jsonPath("$.downvotes", is(1)));
    }

    @Test
    @WithMockUser
    void getPostEditDetails() throws Exception {
        String postId = "d92e1999-fd40-4ed8-b72a-faa16b54da4f";
        String fileUrl = "https://www.dropbox.com/s/xfjwduhyzs5wtyy/1010783_815818905111668_384454518_n.jpg?dl=0&raw=1";

        mockMvc.perform(get("/api/post/edit/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("some hoe")))
                .andExpect(jsonPath("$.content", is("do i really need content?")))
                .andExpect(jsonPath("$.subredditTitle", is("aww")))
                .andExpect(jsonPath("$.fileUrl", is(fileUrl)));
    }

    @Test
    @WithAnonymousUser
    void getPostEditDetails_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        String postId = "d92e1999-fd40-4ed8-b72a-faa16b54da4f";
        mockMvc.perform(get("/api/post/edit/" + postId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void vote_withSameVoteChoice_deletesVoteAndReturnsCorrectMessageAndStatus() throws Exception {
        String postId = "f6ccac45-2b61-41be-aa14-eab19d2a1379";
        mockMvc.perform(post("/api/post/vote?choice=1&postId=" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_VOTE_DELETION)));
    }

    @Test
    @WithAnonymousUser
    void vote_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        String postId = "b2ce3fe3-8a3c-474b-b4b0-869fac195d89";
        mockMvc.perform(post("/api/post/vote?choice=1&postId=" + postId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("user")
    void delete_withCreatorUser_deletesPostAndReturnsCorrectStatusAndMessage() throws Exception {
        String postId = "821c2de8-d9ec-4e07-9c00-c8095eb27ed4";
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/post/delete?postId=" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_POST_DELETION)));
    }

    @Test
    @WithMockCustomUser("admin")
    void delete_withAdminUser_deletesPostAndReturnsCorrectStatusAndMessage() throws Exception {
        String postId = "ca95bfad-f810-4cda-965c-154a86761cf9";
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/post/delete?postId=" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_POST_DELETION)));
    }
}