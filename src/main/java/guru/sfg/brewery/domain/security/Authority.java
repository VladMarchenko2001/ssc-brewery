package guru.sfg.brewery.domain.security;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import java.util.Set;

import static javax.persistence.GenerationType.AUTO;

@Data
@Accessors(chain = true)
@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Integer id;
    private String role;
    @ManyToMany(mappedBy = "authorities")
    private Set<User> users;
}
