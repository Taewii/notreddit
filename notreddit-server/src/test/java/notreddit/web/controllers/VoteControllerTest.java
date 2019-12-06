package notreddit.web.controllers;

import notreddit.AbstractIntegrationTest;
import notreddit.web.controllers.utils.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoteControllerTest extends AbstractIntegrationTest {

    @Test
    @WithMockCustomUser("root")
    void getCurrentUserVotesForPosts() throws Exception {
        mockMvc.perform(get("/api/vote/votes-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(7)));
    }

    @Test
    @WithAnonymousUser
    void getCurrentUserVotesForPosts_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/vote/votes-posts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void getCurrentUserVotesForComments() throws Exception {
        mockMvc.perform(get("/api/vote/votes-comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(9)));
    }

    @Test
    @WithAnonymousUser
    void getCurrentUserVotesForComments_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/vote/votes-comments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void getUserVoteForPost_withExistingVote_returnsCorrectData() throws Exception {
        String postId = "d92e1999-fd40-4ed8-b72a-faa16b54da4f";
        mockMvc.perform(get("/api/vote/post?postId=" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasVoted", is(true)))
                .andExpect(jsonPath("$.choice", is(1)));
    }

    @Test
    @WithMockCustomUser("root")
    void getUserVoteForPost_withNonExistentVote_returnsCorrectData() throws Exception {
        String postId = "730e6c67-2fbf-49d0-9762-42364a7841c2";
        mockMvc.perform(get("/api/vote/post?postId=" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasVoted", is(false)))
                .andExpect(jsonPath("$.choice", nullValue()));
    }

    @Test
    @WithAnonymousUser
    void getUserVoteForPos_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        String postId = "d92e1999-fd40-4ed8-b72a-faa16b54da4f";
        mockMvc.perform(get("/api/vote/post?postId=" + postId))
                .andExpect(status().isUnauthorized());
    }
}