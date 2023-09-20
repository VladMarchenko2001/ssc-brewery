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

import guru.sfg.brewery.domain.Beer;
import guru.sfg.brewery.domain.BeerInventory;
import guru.sfg.brewery.domain.BeerOrder;
import guru.sfg.brewery.domain.BeerOrderLine;
import guru.sfg.brewery.domain.Brewery;
import guru.sfg.brewery.domain.Customer;
import guru.sfg.brewery.domain.OrderStatusEnum;
import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.Role;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.BeerInventoryRepository;
import guru.sfg.brewery.repositories.BeerOrderRepository;
import guru.sfg.brewery.repositories.BeerRepository;
import guru.sfg.brewery.repositories.BreweryRepository;
import guru.sfg.brewery.repositories.CustomerRepository;
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
import java.util.UUID;

import static guru.sfg.brewery.domain.security.AuthorityConstants.BEER_CREATING;
import static guru.sfg.brewery.domain.security.AuthorityConstants.BEER_DELETING;
import static guru.sfg.brewery.domain.security.AuthorityConstants.BEER_READING;
import static guru.sfg.brewery.domain.security.AuthorityConstants.BEER_UPDATING;
import static guru.sfg.brewery.domain.security.Role.RoleName.ADMIN;
import static guru.sfg.brewery.domain.security.Role.RoleName.CUSTOMER;
import static guru.sfg.brewery.domain.security.Role.RoleName.USER;


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
        loadBreweryData();
        loadCustomerData();
        loadUserAndAuthoritiesData();
    }

    private void loadCustomerData() {
        Customer tastingRoom = Customer.builder()
                .customerName(TASTING_ROOM)
                .apiKey(UUID.randomUUID())
                .build();

        customerRepository.save(tastingRoom);

        beerRepository.findAll().forEach(beer -> {
            beerOrderRepository.save(BeerOrder.builder()
                    .customer(tastingRoom)
                    .orderStatus(OrderStatusEnum.NEW)
                    .beerOrderLines(Set.of(BeerOrderLine.builder()
                            .beer(beer)
                            .orderQuantity(2)
                            .build()))
                    .build());
        });
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

    @Transactional
    public void loadUserAndAuthoritiesData() {
        //beer authorities
        Authority beerCreatingAuthority = new Authority().setPermission(BEER_CREATING.getAction());
        Authority beerReadingAuthority =  new Authority().setPermission(BEER_READING.getAction());
        Authority beerUpdatingAuthority = new Authority().setPermission(BEER_UPDATING.getAction());
        Authority beerDeletingAuthority = new Authority().setPermission(BEER_DELETING.getAction());

        authorityRepository.saveAll(Set.of(beerCreatingAuthority, beerUpdatingAuthority, beerDeletingAuthority, beerReadingAuthority));

        Role adminRole = roleRepository.save(new Role().setName(ADMIN.getName()));
        Role userRole = roleRepository.save(new Role().setName(USER.getName()));
        Role customerRole = roleRepository.save(new Role().setName(CUSTOMER.getName()));

        adminRole.setAuthorities(Set.of(beerCreatingAuthority, beerUpdatingAuthority, beerDeletingAuthority, beerReadingAuthority));
        userRole.setAuthorities(Set.of(beerReadingAuthority));
        customerRole.setAuthorities(Set.of(beerReadingAuthority));

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
}
