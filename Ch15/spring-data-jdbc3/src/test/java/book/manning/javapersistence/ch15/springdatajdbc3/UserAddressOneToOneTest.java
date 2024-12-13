package book.manning.javapersistence.ch15.springdatajdbc3;

import book.manning.javapersistence.ch15.springdatajdbc3.model.Address;
import book.manning.javapersistence.ch15.springdatajdbc3.model.User;
import book.manning.javapersistence.ch15.springdatajdbc3.repositories.AddressOneToOneRepository;
import book.manning.javapersistence.ch15.springdatajdbc3.repositories.UserOneToOneRepository;
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
class UserAddressOneToOneTest {

    @Autowired
    private UserOneToOneRepository userOneToOneRepository;

    @Autowired
    private AddressOneToOneRepository addressOneToOneRepository;

    private static List<User> users = new ArrayList<>();

    @BeforeAll
    void beforeAll() {
        userOneToOneRepository.saveAll(generateUsers());
    }

    @AfterAll
    void afterAll() {
        userOneToOneRepository.deleteAll();
    }

    @Test
    void oneToOneTest() {
        User john = userOneToOneRepository.findByUsername("john");
        assertThat(john).isNotNull();
        assertThat(john.getAddress().getCity()).isEqualTo("New York");
    }

    @Test
    void countTest() {
        assertAll(
                () -> assertThat(userOneToOneRepository.count()).isEqualTo(10),
                () -> assertThat(addressOneToOneRepository.count()).isEqualTo(10)
        );
    }

    private static Address generateAddress(String number) {
        return new Address("New York", number + ", 5th Avenue");
    }

    private List<User> generateUsers() {
        User john = new User("john", LocalDate.of(2020, Month.APRIL, 13));
        john.setEmail("john@somedomain.com");
        john.setLevel(1);
        john.setActive(true);
        john.setAddress(generateAddress("1"));

        User mike = new User("mike", LocalDate.of(2020, Month.JANUARY, 18));
        mike.setEmail("mike@somedomain.com");
        mike.setLevel(3);
        mike.setActive(true);
        mike.setAddress(generateAddress("2"));

        User james = new User("james", LocalDate.of(2020, Month.MARCH, 11));
        james.setEmail("james@someotherdomain.com");
        james.setLevel(3);
        james.setActive(false);
        james.setAddress(generateAddress("3"));

        User katie = new User("katie", LocalDate.of(2021, Month.JANUARY, 5));
        katie.setEmail("katie@somedomain.com");
        katie.setLevel(5);
        katie.setActive(true);
        katie.setAddress(generateAddress("4"));

        User beth = new User("beth", LocalDate.of(2020, Month.AUGUST, 3));
        beth.setEmail("beth@somedomain.com");
        beth.setLevel(2);
        beth.setActive(true);
        beth.setAddress(generateAddress("5"));

        User julius = new User("julius", LocalDate.of(2021, Month.FEBRUARY, 9));
        julius.setEmail("julius@someotherdomain.com");
        julius.setLevel(4);
        julius.setActive(true);
        julius.setAddress(generateAddress("6"));

        User darren = new User("darren", LocalDate.of(2020, Month.DECEMBER, 11));
        darren.setEmail("darren@somedomain.com");
        darren.setLevel(2);
        darren.setActive(true);
        darren.setAddress(generateAddress("7"));

        User marion = new User("marion", LocalDate.of(2020, Month.SEPTEMBER, 23));
        marion.setEmail("marion@someotherdomain.com");
        marion.setLevel(2);
        marion.setActive(false);
        marion.setAddress(generateAddress("8"));

        User stephanie = new User("stephanie", LocalDate.of(2020, Month.JANUARY, 18));
        stephanie.setEmail("stephanie@someotherdomain.com");
        stephanie.setLevel(4);
        stephanie.setActive(true);
        stephanie.setAddress(generateAddress("9"));

        User burk = new User("burk", LocalDate.of(2020, Month.NOVEMBER, 28));
        burk.setEmail("burk@somedomain.com");
        burk.setLevel(1);
        burk.setActive(true);
        burk.setAddress(generateAddress("10"));

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
