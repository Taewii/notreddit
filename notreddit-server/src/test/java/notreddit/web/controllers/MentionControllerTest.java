package notreddit.web.controllers;

import notreddit.AbstractIntegrationTest;
import notreddit.web.controllers.utils.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;

import static notreddit.constants.ApiResponseMessages.MENTION_MARKED_AS;
import static notreddit.constants.ApiResponseMessages.NONEXISTENT_MENTION_OR_NOT_RECEIVER;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MentionControllerTest extends AbstractIntegrationTest {

    @Test
    @WithMockCustomUser("root")
    void getUsersUnreadMentionsCount() throws Exception {
        mockMvc.perform(get("/api/mention/unread-mentions-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(5)));
    }

    @Test
    @WithAnonymousUser
    void getUsersUnreadMentionsCount_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/mention/unread-mentions-count"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void getUsersMentions() throws Exception {
        mockMvc.perform(get("/api/mention/user-mentions?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(8)))
                .andExpect(jsonPath("$.mentions.length()", is(5)));
    }

    @Test
    @WithAnonymousUser
    void getUsersMentions_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/mention/user-mentions?page=0&size=5"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void markRead() throws Exception {
        String mentionId = "c1ab8fd0-db92-49c2-90c4-dac47a1f467e";
        String responseMessage = String.format(MENTION_MARKED_AS, "read");

        mockMvc.perform(post("/api/mention/read?mentionId=" + mentionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(responseMessage)));
    }

    @Test
    @WithAnonymousUser
    void markRead_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        String mentionId = "c1ab8fd0-db92-49c2-90c4-dac47a1f467e";
        mockMvc.perform(post("/api/mention/read?mentionId=" + mentionId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void markRead_withMentionThatHasADifferentReceiverThanTheCurrentUser_returnsCorrectStatusAndMessage() throws Exception {
        String mentionId = "a4816f9e-063d-41c7-8a95-415bae45768b";

        mockMvc.perform(post("/api/mention/read?mentionId=" + mentionId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(NONEXISTENT_MENTION_OR_NOT_RECEIVER)));
    }

    @Test
    @WithMockCustomUser("root")
    void markUnread() throws Exception {
        String mentionId = "c1ab8fd0-db92-49c2-90c4-dac47a1f467e";
        String responseMessage = String.format(MENTION_MARKED_AS, "unread");

        mockMvc.perform(post("/api/mention/unread?mentionId=" + mentionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(responseMessage)));
    }

    @Test
    @WithMockCustomUser("root")
    void markUnread_withMentionThatHasADifferentReceiverThanTheCurrentUser_returnsCorrectStatusAndMessage() throws Exception {
        String mentionId = "a4816f9e-063d-41c7-8a95-415bae45768b";

        mockMvc.perform(post("/api/mention/unread?mentionId=" + mentionId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(NONEXISTENT_MENTION_OR_NOT_RECEIVER)));
    }

    @Test
    @WithAnonymousUser
    void markUnread_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        String mentionId = "c1ab8fd0-db92-49c2-90c4-dac47a1f467e";
        mockMvc.perform(post("/api/mention/unread?mentionId=" + mentionId))
                .andExpect(status().isUnauthorized());
    }
}