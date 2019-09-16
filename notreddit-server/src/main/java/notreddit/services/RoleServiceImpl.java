package notreddit.services;

import notreddit.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<String> getAllAsStrings() {
        return roleRepository
                .findAll()
                .stream()
                .map(r -> r.getAuthority().substring("ROLE_".length()))
                .collect(Collectors.toUnmodifiableList());
    }
}
