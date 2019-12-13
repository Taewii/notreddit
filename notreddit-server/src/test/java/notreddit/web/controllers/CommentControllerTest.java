package notreddit.web.controllers;

import notreddit.domain.models.requests.CommentCreateRequest;
import notreddit.domain.models.requests.CommentEditRequest;
import notreddit.web.controllers.utils.AbstractIntegrationTest;
import notreddit.web.controllers.utils.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static notreddit.constants.ApiResponseMessages.*;
import static notreddit.constants.ErrorMessages.ACCESS_FORBIDDEN;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends AbstractIntegrationTest {

    @Test
    @WithMockCustomUser("root")
    void createComment() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("content");
        request.setParentId(null);
        request.setPostId(UUID.fromString("d92e1999-fd40-4ed8-b72a-faa16b54da4f"));

        mockMvc.perform(post("/api/comment/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_COMMENT_CREATION)));
    }

    @Test
    @WithMockCustomUser("root")
    void createComment_withNonExistentPost_returnsCorrectStatusAndMessage() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("content");
        request.setParentId(null);
        request.setPostId(UUID.randomUUID());

        mockMvc.perform(post("/api/comment/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(NONEXISTENT_POST)));
    }

    @Test
    @WithAnonymousUser
    void createComment_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setContent("content");
        request.setParentId(null);
        request.setPostId(UUID.fromString("d92e1999-fd40-4ed8-b72a-faa16b54da4f"));

        mockMvc.perform(post("/api/comment/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void findAllFromPost() throws Exception {
        String postId = "21c8152f-61cc-4b88-a48d-0771e1396abb";
        mockMvc.perform(get("/api/comment/post?postId=" + postId + "&sort=upvotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }

    @Test
    @WithAnonymousUser
    void findAllByUsername() throws Exception {
        mockMvc.perform(get("/api/comment/user/root?page=0&size=5&sort=upvotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(12)))
                .andExpect(jsonPath("$.comments.length()", is(5)));
    }

    @Test
    @WithMockCustomUser("root")
    void vote_withSameVoteChoice_deletesVoteAndReturnsCorrectMessageAndStatus() throws Exception {
        String commentId = "14c6c3af-5f09-4f31-a5e1-88f2bb26be81";
        mockMvc.perform(post("/api/comment/vote?choice=-1&commentId=" + commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_VOTE_DELETION)));
    }

    @Test
    @WithAnonymousUser
    void vote__withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        String commentId = "d92e1999-fd40-4ed8-b72a-faa16b54da4f";
        mockMvc.perform(post("/api/comment/vote?choice=1&commentId=" + commentId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("user")
    void delete_withCreatorUser_returnsCorrectStatusAndMessage() throws Exception {
        String commentId = "fb7315c8-65ec-417f-8b75-eabf98fd3eca";
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comment/delete?commentId=" + commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_COMMENT_DELETION)));
    }

    @Test
    @WithMockCustomUser("user")
    void delete_withNormalUser_returnsForbiddenStatus() throws Exception {
        String commentId = "d5ac3df1-9c6e-40fc-b26e-99fbc909ef70";
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comment/delete?commentId=" + commentId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(ACCESS_FORBIDDEN)));
    }

    @Test
    @WithAnonymousUser
    void delete_withAnonymousUser_returnsUnauthorizedStatus() throws Exception {
        String commentId = "fb7315c8-65ec-417f-8b75-eabf98fd3eca";
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comment/delete?commentId=" + commentId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser("user")
    void edit_withCreatorUser_editsCommentAndReturnsCorrectStatusAndMessage() throws Exception {
        CommentEditRequest requestModel = new CommentEditRequest();
        requestModel.setContent("content");
        requestModel.setCommentId(UUID.fromString("fb7315c8-65ec-417f-8b75-eabf98fd3eca"));

        mockMvc.perform(patch("/api/comment/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is(SUCCESSFUL_COMMENT_EDITING)));
    }

    @Test
    @WithMockCustomUser("moderator")
    void edit_withUserThatIsNotTheCreator_returnsCorrectStatusAndMessage() throws Exception {
        CommentEditRequest requestModel = new CommentEditRequest();
        requestModel.setContent("content");
        requestModel.setCommentId(UUID.fromString("fb7315c8-65ec-417f-8b75-eabf98fd3eca"));

        mockMvc.perform(patch("/api/comment/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(NONEXISTENT_COMMENT_OR_NOT_CREATOR)));
    }

    @Test
    @WithMockCustomUser("user")
    void edit_withNonExistentCommentId_returnsCorrectStatusAndMessage() throws Exception {
        CommentEditRequest requestModel = new CommentEditRequest();
        requestModel.setContent("content");
        requestModel.setCommentId(UUID.randomUUID());

        mockMvc.perform(patch("/api/comment/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestModel)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is(NONEXISTENT_COMMENT_OR_NOT_CREATOR)));
    }
}