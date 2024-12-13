package book.manning.javapersistence.ch15.springdatajdbc6;

import book.manning.javapersistence.ch15.springdatajdbc6.model.Address;
import book.manning.javapersistence.ch15.springdatajdbc6.model.User;
import book.manning.javapersistence.ch15.springdatajdbc6.repositories.AddressManyToManyRepository;
import book.manning.javapersistence.ch15.springdatajdbc6.repositories.UserAddressManyToManyRepository;
import book.manning.javapersistence.ch15.springdatajdbc6.repositories.UserManyToManyRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserAddressManyToManyTest {

    @Autowired
    private UserAddressManyToManyRepository userAddressManyToManyRepository;

    @Autowired
    private AddressManyToManyRepository addressManyToManyRepository;

    @Autowired
    private UserManyToManyRepository userManyToManyRepository;

    private List<User> users = new ArrayList<>();

    private Address address1 = generateAddress("11");
    private Address address2 = generateAddress("22");
    private Address address3 = generateAddress("33");

    @BeforeAll
    void beforeAll() {
        addressManyToManyRepository.save(address1);
        addressManyToManyRepository.save(address2);
        addressManyToManyRepository.save(address3);
        userManyToManyRepository.saveAll(generateUsers());
    }

    @AfterAll
    void afterAll() {
        addressManyToManyRepository.deleteAll();
        userManyToManyRepository.deleteAll();
    }

    @Test
    void manyToManyTest() {
        assertAll(
                () -> assertThat(userManyToManyRepository.count()).isEqualTo(10),
                () -> assertThat(addressManyToManyRepository.count()).isEqualTo(3),
                () -> assertThat(userAddressManyToManyRepository.count()).isEqualTo(20),
                () -> assertThat(userAddressManyToManyRepository.countByUserId(users.get(0).getId()))
        );
    }

    private Address generateAddress(String number) {
        return new Address("New York", number + ", 5th Avenue");
    }

    private List<User> generateUsers() {
        User john = new User("john", LocalDate.of(2020, Month.APRIL, 13));
        john.setEmail("john@somedomain.com");
        john.setLevel(1);
        john.setActive(true);
        john.addAddress(address1);
        john.addAddress(address2);

        User mike = new User("mike", LocalDate.of(2020, Month.JANUARY, 18));
        mike.setEmail("mike@somedomain.com");
        mike.setLevel(3);
        mike.setActive(true);
        mike.addAddress(address1);
        mike.addAddress(address3);

        User james = new User("james", LocalDate.of(2020, Month.MARCH, 11));
        james.setEmail("james@someotherdomain.com");
        james.setLevel(3);
        james.setActive(false);
        james.addAddress(address2);
        james.addAddress(address3);

        User katie = new User("katie", LocalDate.of(2021, Month.JANUARY, 5));
        katie.setEmail("katie@somedomain.com");
        katie.setLevel(5);
        katie.setActive(true);
        katie.addAddress(address1);
        katie.addAddress(address2);

        User beth = new User("beth", LocalDate.of(2020, Month.AUGUST, 3));
        beth.setEmail("beth@somedomain.com");
        beth.setLevel(2);
        beth.setActive(true);
        beth.addAddress(address1);
        beth.addAddress(address3);

        User julius = new User("julius", LocalDate.of(2021, Month.FEBRUARY, 9));
        julius.setEmail("julius@someotherdomain.com");
        julius.setLevel(4);
        julius.setActive(true);
        julius.addAddress(address2);
        julius.addAddress(address3);

        User darren = new User("darren", LocalDate.of(2020, Month.DECEMBER, 11));
        darren.setEmail("darren@somedomain.com");
        darren.setLevel(2);
        darren.setActive(true);
        darren.addAddress(address1);
        darren.addAddress(address2);

        User marion = new User("marion", LocalDate.of(2020, Month.SEPTEMBER, 23));
        marion.setEmail("marion@someotherdomain.com");
        marion.setLevel(2);
        marion.setActive(false);
        marion.addAddress(address1);
        marion.addAddress(address3);

        User stephanie = new User("stephanie", LocalDate.of(2020, Month.JANUARY, 18));
        stephanie.setEmail("stephanie@someotherdomain.com");
        stephanie.setLevel(4);
        stephanie.setActive(true);
        stephanie.addAddress(address2);
        stephanie.addAddress(address3);

        User burk = new User("burk", LocalDate.of(2020, Month.NOVEMBER, 28));
        burk.setEmail("burk@somedomain.com");
        burk.setLevel(1);
        burk.setActive(true);
        burk.addAddress(address1);
        burk.addAddress(address2);

        users.add(john);
        users.add(mike);
        users.add(james);
        users.add(katie);
        users.add(beth);
        users.add(julius);
        users.add(darren);
        users.add(marion);
        users.add(stephanie);
        users.add(burk);

        return users;
    }
}
