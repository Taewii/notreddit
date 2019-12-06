package notreddit.web.controllers;

import notreddit.AbstractIntegrationTest;
import notreddit.web.controllers.utils.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleControllerTest extends AbstractIntegrationTest {

    @Test
    @WithMockCustomUser("admin")
    void getAllRoles_withAdminUser_returnsCorrectResponse() throws Exception {
        mockMvc.perform(get("/api/role/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles", hasSize(4)));
    }

    @Test
    @WithMockCustomUser("user")
    void getAllRoles_withNormalUser_returnsForbiddenStatus() throws Exception {
        mockMvc.perform(get("/api/role/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void getAllRoles_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        mockMvc.perform(get("/api/role/all"))
                .andExpect(status().isUnauthorized());

    }
}