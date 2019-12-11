package notreddit.web.controllers;

import lombok.RequiredArgsConstructor;
import notreddit.domain.models.responses.role.RolesResponse;
import notreddit.services.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public RolesResponse getAllRoles() {
        List<String> roles = roleService.getAllAsStrings();
        return new RolesResponse(roles);
    }
}
