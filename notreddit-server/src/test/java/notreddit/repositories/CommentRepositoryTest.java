package notreddit.repositories;

import notreddit.PostgreSQLContainerInitializer;
import notreddit.domain.entities.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findByPostIdWithChildren() {
        UUID postId = UUID.fromString("d92e1999-fd40-4ed8-b72a-faa16b54da4f");
        Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
        List<Comment> comments = commentRepository.findByPostIdWithChildren(postId, sort);

        assertEquals(3, comments.size());

        List<Comment> sortedComments = new ArrayList<>(comments);
        sortedComments.sort((a, b) -> b.getCreatedOn().compareTo(a.getCreatedOn()));

        for (int i = 0; i < comments.size(); i++) {
            assertEquals(comments.get(i).getPost().getId(), postId);
            assertEquals(comments.get(i), sortedComments.get(i));
        }
    }

    @Test
    void findByCreatorUsername_withUserWithComments_returnsCorrectData() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdOn"));
        Page<Comment> page = commentRepository.findByCreatorUsername("root", pageable);

        assertEquals(11, page.getTotalElements());
        assertEquals(5, page.getContent().size());
        assertEquals(3, page.getTotalPages());
        page.getContent().forEach(c -> assertEquals("root", c.getCreator().getUsername()));
    }

    @Test
    void findByCreatorUsername_withUserWithNoComments_returnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdOn"));
        Page<Comment> page = commentRepository.findByCreatorUsername("moderator", pageable);

        assertEquals(0, page.getTotalElements());
        assertEquals(0, page.getContent().size());
        assertEquals(0, page.getTotalPages());
    }
}