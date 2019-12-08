package notreddit.web.controllers;

import notreddit.domain.models.requests.ChangeRoleRequest;
import notreddit.web.controllers.utils.AbstractIntegrationTest;
import notreddit.web.controllers.utils.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static notreddit.constants.ApiResponseMessages.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends AbstractIntegrationTest {

    @Test
    @WithAnonymousUser
    void checkUsernameAvailability_withAvailableName_returnsTrue() throws Exception {
        String username = "available";
        mockMvc.perform(get("/api/user/check-username-availability?username=" + username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    @WithAnonymousUser
    void checkUsernameAvailability_withExistingName_returnsFalse() throws Exception {
        String username = "user";
        mockMvc.perform(get("/api/user/check-username-availability?username=" + username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)));
    }

    @Test
    @WithMockCustomUser("user")
    void checkUsernameAvailability_withAuthenticatedUser_returnsForbiddenStatus() throws Exception {
        String username = "user";
        mockMvc.perform(get("/api/user/check-username-availability?username=" + username))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void checkEmailAvailability_withAvailableEmail_returnsTrue() throws Exception {
        String email = "available@abv.bg";
        mockMvc.perform(get("/api/user/check-email-availability?email=" + email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    @WithAnonymousUser
    void checkEmailAvailability_withExistingEmail_returnsFalse() throws Exception {
        String email = "email@abv.bg";
        mockMvc.perform(get("/api/user/check-email-availability?email=" + email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)));
    }

    @Test
    @WithMockCustomUser("user")
    void checkEmailAvailability_withAuthenticatedUser_returnsForbiddenStatus() throws Exception {
        String email = "available@abv.bg";
        mockMvc.perform(get("/api/user/check-email-availability?email=" + email))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser("moderator")
    void getCurrentUser() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("moderator")))
                .andExpect(jsonPath("$.roles.length()", is(2)));
    }

    @Test
    @WithAnonymousUser
    void getCurrentUser_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users.length()", is(4)));
    }

    @Test
    @WithMockCustomUser("moderator")
    void getAllUsers_withUnauthorizedUser_returnsForbiddenStatus() throws Exception {
        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void getAllUsers_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void changeUserRole() throws Exception {
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setUserId(UUID.fromString("24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71"));
        request.setCurrentRole("USER");
        request.setNewRole("MODERATOR");

        String responseMessage = String.format(CHANGED_USERS_ROLE_TO, "user", request.getNewRole());
        mockMvc.perform(post("/api/user/change-role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(responseMessage)));

        request.setNewRole("USER");
        responseMessage = String.format(CHANGED_USERS_ROLE_TO, "user", request.getNewRole());
        mockMvc.perform(post("/api/user/change-role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(responseMessage)));
    }

    @Test
    @WithMockCustomUser("root")
    void changeUserRole_tryingToChangeFromRootRoleOrToRootRole_returnsCorrectMessageAndStatus() throws Exception {
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setUserId(UUID.fromString("24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71"));
        request.setCurrentRole("ROOT");
        request.setNewRole("ROOT");

        mockMvc.perform(post("/api/user/change-role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(CANNOT_CHANGE_ROLE_TO_FROM_ROOT)));
    }

    @Test
    @WithMockCustomUser("moderator")
    void changeUserRole_withUnauthorizedUser_returnsForbiddenStatus() throws Exception {
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setUserId(UUID.fromString("24cc00bb-7b43-4e69-ac5c-f0b85d5fdc71"));
        request.setCurrentRole("USER");
        request.setNewRole("MODERATOR");

        mockMvc.perform(post("/api/user/change-role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser("root")
    void changeUserRole_withNonExistentUser_returnsCorrectStatusAndMessage() throws Exception {
        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setUserId(UUID.randomUUID());
        request.setCurrentRole("USER");
        request.setNewRole("MODERATOR");

        mockMvc.perform(post("/api/user/change-role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(NONEXISTENT_USER_ID)));
    }

    @Test
    @Transactional
    @WithMockCustomUser("root")
    void deleteUser() throws Exception {
        String userId = "46cf2027-3503-4168-9d58-c5f4b81db30a";

        String responseMessage = String.format(SUCCESSFUL_USER_DELETION, "admin");
        mockMvc.perform(delete("/api/user/delete?id=" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(responseMessage)));
    }

    @Test
    @Transactional
    @WithMockCustomUser("root")
    void deleteUser_withNonExistentUser_returnsCorrectStatusAndMessage() throws Exception {
        String userId = UUID.randomUUID().toString();

        String responseMessage = String.format(NONEXISTENT_USER_WITH_ID, userId);
        mockMvc.perform(delete("/api/user/delete?id=" + userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(responseMessage)));
    }

    @Test
    @Transactional
    @WithMockCustomUser("moderator")
    void deleteUser_withUnauthorizedUser_returnsForbiddenStatus() throws Exception {
        String userId = "46cf2027-3503-4168-9d58-c5f4b81db30a";

        mockMvc.perform(delete("/api/user/delete?id=" + userId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser("root")
    void getUpvotedPosts() throws Exception {
        mockMvc.perform(get("/api/user/root/upvoted?page=0&size=5&sort=upvotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(5)))
                .andExpect(jsonPath("$.posts", hasSize(5)));
    }

    @Test
    @WithMockCustomUser("root")
    void getUpvotedPosts_withUserThatIsNotTheCurrentlyLoggedOne_returnsForbiddenStatus() throws Exception {
        mockMvc.perform(get("/api/user/user/upvoted?page=0&size=5&sort=upvotes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void getUpvotedPosts_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/user/root/upvoted?page=0&size=5&sort=upvotes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void getDownvotedPosts() throws Exception {
        mockMvc.perform(get("/api/user/root/downvoted?page=0&size=5&sort=upvotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.posts", hasSize(2)));
    }

    @Test
    @WithAnonymousUser
    void getDownvotedPosts_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/user/root/downvoted?page=0&size=5&sort=upvotes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("root")
    void getDownvotedPosts_withUserThatIsNotTheCurrentlyLoggedOne_returnsForbiddenStatus() throws Exception {
        mockMvc.perform(get("/api/user/user/downvoted?page=0&size=5&sort=upvotes"))
                .andExpect(status().isForbidden());
    }
}