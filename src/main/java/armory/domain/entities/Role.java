package armory.domain.entities;

import armory.domain.enums.Authority;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role extends BaseLongEntity implements GrantedAuthority {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<Account> accounts = new HashSet<>();

    public Role(Authority authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority.asRole();
    }

    public Authority authorityAsEnum() {
        return this.authority;
    }
}
