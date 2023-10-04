/*
 *  Copyright 2020 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.*;
import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.*;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import guru.sfg.brewery.web.model.BeerStyleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static guru.sfg.brewery.domain.OrderStatusEnum.NEW;
import static guru.sfg.brewery.domain.security.AuthorityConstants.*;
import static guru.sfg.brewery.domain.security.Role.RoleName.*;
import static java.util.UUID.randomUUID;


/**
 * Created by jt on 2019-01-26.
 */
@RequiredArgsConstructor
@Component
public class DefaultBreweryLoader implements CommandLineRunner {

    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    private final PasswordEncoder passwordEncoder;

    public static final String TASTING_ROOM = "Tasting Room";
    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    @Override
    public void run(String... args) {
        loadUserAndAuthoritiesData();
        loadBreweryData();
        loadTestingRoomData();
        loadCustomerData();
    }

    private void loadUserAndAuthoritiesData() {
        //beer authorities
        Authority beerCreatingAuthority = new Authority().setPermission(BEER_CREATING.getAction());
        Authority beerReadingAuthority =  new Authority().setPermission(BEER_READING.getAction());
        Authority beerUpdatingAuthority = new Authority().setPermission(BEER_UPDATING.getAction());
        Authority beerDeletingAuthority = new Authority().setPermission(BEER_DELETING.getAction());

        //customer authorities
        Authority customerCreatingAuthority = new Authority().setPermission(CUSTOMER_CREATING.getAction());
        Authority customerReadingAuthority =  new Authority().setPermission(CUSTOMER_READING.getAction());
        Authority customerUpdatingAuthority = new Authority().setPermission(CUSTOMER_UPDATING.getAction());
        Authority customerDeletingAuthority = new Authority().setPermission(CUSTOMER_DELETING.getAction());

        //brewery authorities
        Authority breweryCreatingAuthority = new Authority().setPermission(BREWERY_CREATING.getAction());
        Authority breweryReadingAuthority =  new Authority().setPermission(BREWERY_READING.getAction());
        Authority breweryUpdatingAuthority = new Authority().setPermission(BREWERY_UPDATING.getAction());
        Authority breweryDeletingAuthority = new Authority().setPermission(BREWERY_DELETING.getAction());

        //order authorities
        Authority orderCreatingAuthority = new Authority().setPermission(ORDER_CREATING.getAction());
        Authority orderReadingAuthority =  new Authority().setPermission(ORDER_READING.getAction());
        Authority orderUpdatingAuthority = new Authority().setPermission(ORDER_UPDATING.getAction());
        Authority orderDeletingAuthority = new Authority().setPermission(ORDER_DELETING.getAction());

        //customer order authorities
        Authority customerOrderCreatingAuthority = new Authority().setPermission(CUSTOMER_ORDER_CREATING.getAction());
        Authority customerOrderReadingAuthority =  new Authority().setPermission(CUSTOMER_ORDER_READING.getAction());
        Authority customerOrderUpdatingAuthority = new Authority().setPermission(CUSTOMER_ORDER_UPDATING.getAction());
        Authority customerOrderDeletingAuthority = new Authority().setPermission(CUSTOMER_ORDER_DELETING.getAction());

        authorityRepository.saveAll(Set.of(beerCreatingAuthority, beerUpdatingAuthority, beerDeletingAuthority, beerReadingAuthority));

        Role adminRole = roleRepository.save(new Role().setName(ADMIN.getName()));
        Role userRole = roleRepository.save(new Role().setName(USER.getName()));
        Role customerRole = roleRepository.save(new Role().setName(CUSTOMER.getName()));

        adminRole.setAuthorities(Set.of(
                beerCreatingAuthority, beerReadingAuthority, beerUpdatingAuthority, beerDeletingAuthority,
                customerCreatingAuthority, customerReadingAuthority, customerUpdatingAuthority, customerDeletingAuthority,
                breweryCreatingAuthority, breweryReadingAuthority, breweryUpdatingAuthority, breweryDeletingAuthority,
                orderCreatingAuthority, orderReadingAuthority, orderUpdatingAuthority, orderDeletingAuthority));

        customerRole.setAuthorities(Set.of(
                beerReadingAuthority, breweryReadingAuthority,
                customerOrderCreatingAuthority, customerOrderReadingAuthority, customerOrderUpdatingAuthority, customerOrderDeletingAuthority));

        userRole.setAuthorities(Set.of(beerReadingAuthority));

        roleRepository.saveAll(Set.of(adminRole, userRole, customerRole));

        User admin = userRepository.save(new User()
                .setUsername("admin")
                .setPassword(passwordEncoder.encode("admin")));

        admin.setRoles(Set.of(adminRole));
        adminRole.setUsers(Set.of(admin));

        User user = userRepository.save(new User()
                .setUsername("user")
                .setPassword(passwordEncoder.encode("user")));

        user.setRoles(Set.of(userRole));


        User customer = userRepository.save(new User()
                .setUsername("customer")
                .setPassword(passwordEncoder.encode("customer")));

        customer.setRoles(Set.of(customerRole));

        userRepository.saveAll(Set.of(admin, user, customer));
    }

    private void loadBreweryData() {
        if (breweryRepository.count() == 0) {
            breweryRepository.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());

            Beer mangoBobs = Beer.builder()
                    .beerName("Mango Bobs")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_1_UPC)
                    .build();

            beerRepository.save(mangoBobs);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(mangoBobs)
                    .quantityOnHand(500)
                    .build());

            Beer galaxyCat = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_2_UPC)
                    .build();

            beerRepository.save(galaxyCat);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(galaxyCat)
                    .quantityOnHand(500)
                    .build());

            Beer pinball = Beer.builder()
                    .beerName("Pinball Porter")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(BEER_3_UPC)
                    .build();

            beerRepository.save(pinball);
            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(pinball)
                    .quantityOnHand(500)
                    .build());

        }
    }

    private void loadTestingRoomData() {
        Customer tastingRoom = new Customer()
                .setCustomerName(TASTING_ROOM)
                .setApiKey(randomUUID());

        customerRepository.save(tastingRoom);

        beerRepository.findAll().forEach(beer -> {
            beerOrderRepository.save(new BeerOrder()
                    .setCustomer(tastingRoom)
                    .setOrderStatus(NEW)
                    .setBeerOrderLines(Set.of(new BeerOrderLine()
                            .setBeer(beer)
                            .setOrderQuantity(2))));
        });
    }

    private void loadCustomerData() {
        Role customerRole = roleRepository.findByName(CUSTOMER.getName()).orElseThrow();

        Customer stPeteCustomer = new Customer()
                .setCustomerName("St Pete Customer")
                .setApiKey(randomUUID());

        Customer dunedinCustomer = new Customer()
                .setCustomerName("Dunedin Customer")
                .setApiKey(randomUUID());

        Customer keyWestCustomer = new Customer()
                .setCustomerName("Key West Customer")
                .setApiKey(randomUUID());

        customerRepository.saveAll(Set.of(stPeteCustomer, dunedinCustomer, keyWestCustomer));

        User stPeteUser = new User()
                .setUsername("stPeteUser")
                .setCustomer(stPeteCustomer)
                .setRoles(Set.of(customerRole))
                .setPassword(passwordEncoder.encode("stPeteUser"));

        User dunedinUser = new User()
                .setUsername("dunedinUser")
                .setCustomer(dunedinCustomer)
                .setRoles(Set.of(customerRole))
                .setPassword(passwordEncoder.encode("dunedinUser"));

        User keyWestUser = new User()
                .setUsername("keyWestUser")
                .setCustomer(keyWestCustomer)
                .setRoles(Set.of(customerRole))
                .setPassword(passwordEncoder.encode("keyWestUser"));

        userRepository.saveAll(Set.of(stPeteUser, dunedinUser, keyWestUser));
        
        createOrder(stPeteCustomer);
        createOrder(dunedinCustomer);
        createOrder(keyWestCustomer);
    }

    private BeerOrder createOrder(Customer customer) {
        return beerOrderRepository.save(new BeerOrder()
                .setCustomer(customer)
                .setOrderStatus(NEW)
                .setBeerOrderLines(Set.of(new BeerOrderLine()
                        .setBeer(beerRepository.findByUpc(BEER_1_UPC))
                        .setOrderQuantity(2))));
    }
}
