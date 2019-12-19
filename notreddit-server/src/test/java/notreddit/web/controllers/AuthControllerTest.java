package notreddit.web.controllers;

import notreddit.data.models.requests.SignInRequest;
import notreddit.data.models.requests.SignUpRequest;
import notreddit.web.controllers.utils.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import static notreddit.constants.ApiResponseMessages.*;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractIntegrationTest {

    @Test
    @WithAnonymousUser
    void register_withValidData_createsUserAndReturnsCorrectStatusAndMessage() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("username");
        request.setPassword("password");
        request.setConfirmPassword("password");
        request.setEmail("billgates@gmail.com");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_USER_REGISTRATION)));
    }

    @Test
    @WithAnonymousUser
    void register_withExistingUsername_returnsCorrectStatusAndMessage() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("root");
        request.setPassword("password");
        request.setConfirmPassword("password");
        request.setEmail("billgates@gmail.com");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(USERNAME_IS_TAKEN)));
    }

    @Test
    @WithAnonymousUser
    void register_withExistingEmail_returnsCorrectStatusAndMessage() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("username");
        request.setPassword("password");
        request.setConfirmPassword("password");
        request.setEmail("email@abv.bg");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(EMAIL_IS_TAKEN)));
    }

    @Test
    @WithAnonymousUser
    void register_withAuthenticatedUser_returnsBadRequestStatus() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setUsername("username");
        request.setPassword("password");
        request.setConfirmPassword("password");
        request.setEmail("billgates@abv.bg");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    void login_withAnonymousUser_logsInUserReturnsJWTAccessTokenAndTokenType() throws Exception {
        SignInRequest request = new SignInRequest();
        request.setUsernameOrEmail("user");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", any(String.class)))
                .andExpect(jsonPath("$.tokenType", is("Bearer")));

    }

    @Test
    @WithMockUser
    void login_withAuthenticatedUser_returnsForbiddenStatus() throws Exception {
        SignInRequest request = new SignInRequest();
        request.setUsernameOrEmail("user");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}