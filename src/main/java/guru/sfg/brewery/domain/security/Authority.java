package guru.sfg.brewery.domain.security;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import java.util.Set;

import static javax.persistence.GenerationType.AUTO;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Accessors(chain = true)
@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Integer id;
    private String permission;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles;
}
