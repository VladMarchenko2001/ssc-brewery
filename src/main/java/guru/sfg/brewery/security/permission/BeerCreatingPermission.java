package guru.sfg.brewery.security.permission;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@PreAuthorize("hasAuthority(T(guru.sfg.brewery.domain.security.AuthorityConstants).BEER_CREATING.toString())")
public @interface BeerCreatingPermission {
}
