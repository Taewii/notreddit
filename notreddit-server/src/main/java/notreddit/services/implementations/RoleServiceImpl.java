package notreddit.services.implementations;

import lombok.RequiredArgsConstructor;
import notreddit.domain.enums.Authority;
import notreddit.repositories.RoleRepository;
import notreddit.services.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<String> getAllAsStrings() {
        return roleRepository
                .findAll()
                .stream()
                .map(r -> r.getAuthority().substring(Authority.ROLE_PREFIX.length()))
                .collect(Collectors.toList());
    }
}
