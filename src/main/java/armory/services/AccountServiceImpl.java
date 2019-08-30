package armory.services;

import armory.domain.entities.Account;
import armory.domain.entities.Role;
import armory.domain.models.responses.ApiResponse;
import armory.domain.models.requests.SignUpRequest;
import armory.repositories.AccountRepository;
import armory.repositories.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
public class AccountServiceImpl implements AccountService {

    private static final List<String> ROLES = new ArrayList<>() {{
        add("ROOT");
        add("ADMIN");
        add("MODERATOR");
        add("USER");
    }};

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final ModelMapper mapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              RoleRepository roleRepository,
                              PasswordEncoder encoder,
                              ModelMapper mapper) {
        this.accountRepository = accountRepository;

        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return accountRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail)
                );
    }

    @Override
    @Transactional
    public UserDetails loadUserById(UUID id) {
//        return accountRepository.findById(id).orElseThrow(); // TODO: 30.8.2019 г. check which one is necessary
        return accountRepository.findByIdWithRoles(id).orElseThrow();
    }

    @Override
    public ResponseEntity<?> register(SignUpRequest model) {
        if (accountRepository.existsByUsername(model.getUsername())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Username is already taken!"));
        }

        if (accountRepository.existsByEmail(model.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Email Address already in use!"));
        }

        Account account = mapper.map(model, Account.class);
        account.setPassword(encoder.encode(model.getPassword()));

        if (accountRepository.count() == 0) {
            account.setRoles(this.getInheritedRolesFromRole("ROOT"));
        } else {
            account.setRoles(this.getInheritedRolesFromRole("USER"));
        }

        accountRepository.saveAndFlush(account);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/home")
                .buildAndExpand().toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

    private Set<Role> getInheritedRolesFromRole(String role) {
        Set<Role> roles = new HashSet<>();
        for (int i = ROLES.indexOf(role.toUpperCase()); i < ROLES.size(); i++) {
            roles.add(roleRepository.findById(i + 1L).orElseThrow());
        }

        return roles;
    }
}
