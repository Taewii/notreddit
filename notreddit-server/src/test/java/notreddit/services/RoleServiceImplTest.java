package notreddit.services;

import notreddit.domain.entities.Role;
import notreddit.domain.enums.Authority;
import notreddit.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    private List<Role> getRoles() {
        Role rootRole = new Role();
        rootRole.setAuthority(Authority.ROOT);
        Role adminRole = new Role();
        adminRole.setAuthority(Authority.ADMIN);
        Role moderatorRole = new Role();
        moderatorRole.setAuthority(Authority.MODERATOR);
        Role userRole = new Role();
        userRole.setAuthority(Authority.USER);

        return Arrays.asList(rootRole, adminRole, moderatorRole, userRole);
    }

    @Test
    void getAllAsStrings_shouldWorkCorrectly() {
        when(roleRepository.findAll()).thenReturn(this.getRoles());
        List<String> result = roleService.getAllAsStrings();

        assertEquals(4, result.size());
        assertTrue(result.contains("ROOT"));
        assertTrue(result.contains("ADMIN"));
        assertTrue(result.contains("MODERATOR"));
        assertTrue(result.contains("USER"));
    }

    @Test
    void getAllAsStrings_withNoRoles_shouldReturnEmptyList() {
        when(roleRepository.findAll()).thenReturn(new ArrayList<>());
        List<String> result = roleService.getAllAsStrings();

        assertTrue(result.isEmpty());
    }
}