package notreddit.web.controllers;

import notreddit.data.models.requests.SubredditCreateRequest;
import notreddit.web.controllers.utils.AbstractIntegrationTest;
import notreddit.web.controllers.utils.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import static notreddit.constants.ApiResponseMessages.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SubredditControllerTest extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    void checkNameAvailability_withUnavailableEmail_returnsFalse() throws Exception {
        mockMvc.perform(get("/api/subreddit/check-subreddit-availability?title=aww"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)));
    }

    @Test
    @WithMockUser
    void checkNameAvailability_withAvailableEmail_returnsFalse() throws Exception {
        mockMvc.perform(get("/api/subreddit/check-subreddit-availability?title=available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    @WithAnonymousUser
    void checkNameAvailability_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/subreddit/check-subreddit-availability?title=available"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void isUserSubscribedToSubreddit_withSubscribedUser_returnsTrue() throws Exception {
        mockMvc.perform(get("/api/subreddit/is-subscribed?subreddit=aww"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSubscribed", is(true)));

    }

    @Test
    @WithMockCustomUser("root")
    void isUserSubscribedToSubreddit_withNotSubscribedUser_returnsFalse() throws Exception {
        mockMvc.perform(get("/api/subreddit/is-subscribed?subreddit=kanye"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSubscribed", is(false)));
    }

    @Test
    @WithAnonymousUser
    void isUserSubscribedToSubreddit_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/subreddit/is-subscribed?subreddit=aww"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("user")
    void create_withNonExistentSubreddit_successfullyCreatesSubreddit() throws Exception {
        SubredditCreateRequest request = new SubredditCreateRequest();
        request.setTitle("randomSubreddit");

        mockMvc.perform(post("/api/subreddit/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_SUBREDDIT_CREATION)));
    }

    @Test
    @WithMockCustomUser("user")
    void create_withExistingSubreddit_returnsCorrectStatusAndMessage() throws Exception {
        SubredditCreateRequest request = new SubredditCreateRequest();
        request.setTitle("aww");

        mockMvc.perform(post("/api/subreddit/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(SUBREDDIT_ALREADY_EXISTS)));
    }

    @Test
    @WithAnonymousUser
    void create_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        SubredditCreateRequest request = new SubredditCreateRequest();
        request.setTitle("randomSubreddit");

        mockMvc.perform(post("/api/subreddit/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAllSubredditNames_returnsCorrectStatusAndSubredditCount() throws Exception {
        mockMvc.perform(get("/api/subreddit/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(15)));
    }

    @Test
    @WithAnonymousUser
    void getAllSubredditNames_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/subreddit/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void getAllSubredditsWithPostCount_returnsCorrectStatusAndSubredditCount() throws Exception {
        mockMvc.perform(get("/api/subreddit/all-with-post-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(15)));
    }

    @Test
    @WithMockCustomUser("user")
    void subscribe_withExistingSubreddit_subscribesAndReturnsCorrectStatusAndMessage() throws Exception {
        String subreddit = "aww";
        String responseMessage = String.format(SUCCESSFUL_SUBREDDIT_SUBSCRIPTION, subreddit);

        mockMvc.perform(post("/api/subreddit/subscribe?subreddit=" + subreddit))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(responseMessage)));
    }

    @Test
    @WithMockCustomUser("user")
    void subscribe_withNonExistentSubreddit_returnsCorrectStatusAndMessage() throws Exception {
        String subreddit = "nonexistent";

        mockMvc.perform(post("/api/subreddit/subscribe?subreddit=" + subreddit))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(NONEXISTENT_SUBREDDIT)));
    }

    @Test
    @WithAnonymousUser
    void subscribe_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        String subreddit = "aww";
        mockMvc.perform(post("/api/subreddit/subscribe?subreddit=" + subreddit))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockCustomUser("user")
    void unsubscribe_withExistingSubreddit_unsubscribesAndReturnsCorrectStatusAndMessage() throws Exception {
        String subreddit = "aww";
        String responseMessage = String.format(SUCCESSFUL_SUBREDDIT_UNSUBSCRIPTION, subreddit);

        mockMvc.perform(post("/api/subreddit/unsubscribe?subreddit=" + subreddit))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(responseMessage)));
    }

    @Test
    @WithMockCustomUser("user")
    void unsubscribe_withNonExistentSubreddit_returnsCorrectStatusAndMessage() throws Exception {
        String subreddit = "nonexistent";

        mockMvc.perform(post("/api/subreddit/unsubscribe?subreddit=" + subreddit))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(NONEXISTENT_SUBREDDIT)));
    }

    @Test
    @WithAnonymousUser
    void unsubscribe_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        String subreddit = "aww";
        mockMvc.perform(post("/api/subreddit/unsubscribe?subreddit=" + subreddit))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void getUserSubscriptions_withAuthenticatedUser_returnsUsersSubscriptions() throws Exception {
        mockMvc.perform(get("/api/subreddit/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    @WithAnonymousUser
    void getUserSubscriptions__withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/subreddit/subscriptions"))
                .andExpect(status().isUnauthorized());
    }
}