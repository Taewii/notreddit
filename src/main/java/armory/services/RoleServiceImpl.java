package armory.services;

import armory.domain.entities.Role;
import armory.domain.enums.Authority;
import armory.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.seedDatabase();
    }

    private void seedDatabase() {
        if (roleRepository.count() == 0) {
            log.info("Seeding User Roles.");
            roleRepository.saveAndFlush(new Role(Authority.ROOT));
            roleRepository.saveAndFlush(new Role(Authority.ADMIN));
            roleRepository.saveAndFlush(new Role(Authority.MODERATOR));
            roleRepository.saveAndFlush(new Role(Authority.USER));
        }
    }
}
