package guru.sfg.brewery.domain.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthorityConstants {

    //beer auth
    BEER_CREATING("beer.create"),
    BEER_READING("beer.read"),
    BEER_UPDATING("beer.update"),
    BEER_DELETING("beer.delete"),

    //customer auth
    CUSTOMER_CREATING("customer.create"),
    CUSTOMER_READING("customer.read"),
    CUSTOMER_UPDATING("customer.update"),
    CUSTOMER_DELETING("customer.delete"),

    //customer brewery
    BREWERY_CREATING("brewery.create"),
    BREWERY_READING("brewery.read"),
    BREWERY_UPDATING("brewery.update"),
    BREWERY_DELETING("brewery.delete"),

    //order auth
    ORDER_CREATING("order.create"),
    ORDER_READING("order.read"),
    ORDER_UPDATING("order.update"),
    ORDER_DELETING("order.delete"),

    //customer order auth
    CUSTOMER_ORDER_CREATING("customer.order.create"),
    CUSTOMER_ORDER_READING("customer.order.read"),
    CUSTOMER_ORDER_UPDATING("customer.order.update"),
    CUSTOMER_ORDER_DELETING("customer.order.delete");

    private final String action;

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return this.getAction();
    }
}
