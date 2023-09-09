package guru.sfg.brewery.domain.security;

import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Set;

import static javax.persistence.GenerationType.AUTO;

@Data
@Accessors(chain = true)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Integer id;
    private String password;
    private String username;

    @Singular
    @ManyToMany
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private Set<Authority> authorities;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
}
