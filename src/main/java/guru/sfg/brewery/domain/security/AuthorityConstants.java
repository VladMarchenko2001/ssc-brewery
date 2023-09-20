package guru.sfg.brewery.domain.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthorityConstants {

    //beer authorities
    BEER_READING("beer.read"),
    BEER_CREATING("beer.create"),
    BEER_UPDATING("beer.update"),
    BEER_DELETING("beer.delete");

    private final String action;

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return this.getAction();
    }
}
