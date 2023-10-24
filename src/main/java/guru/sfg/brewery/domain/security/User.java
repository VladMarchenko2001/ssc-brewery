package guru.sfg.brewery.domain.security;

import guru.sfg.brewery.domain.Customer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.AUTO;

@ToString
@Data
@Accessors(chain = true)
@Entity
public class User implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Integer id;
    private String password;
    private String username;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Singular
    @ManyToMany(cascade = MERGE, fetch = EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @ManyToOne
    private Customer customer;

    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;

    private boolean isGoogle2FaEnabled;

    @Transient
    private boolean isGoogle2FaRequired = true;

    @Transient
    private Set<Authority> authorities;

    private String google2FaSecret;

    @Transient
    public Set<SimpleGrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(Role::getAuthorities)
                .flatMap(Set::stream)
                .map(authority -> new SimpleGrantedAuthority(authority.getPermission()))
                .collect(toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }
}
