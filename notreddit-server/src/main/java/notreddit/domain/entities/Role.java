package notreddit.domain.entities;

import lombok.Getter;
import lombok.Setter;
import notreddit.domain.enums.Authority;
import org.hibernate.annotations.NaturalId;
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
    @NaturalId
    @Column(nullable = false)
    private Authority authority;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @Override
    public String getAuthority() {
        return authority.asRole();
    }

    public Authority authorityAsEnum() {
        return this.authority;
    }
}
